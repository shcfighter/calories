package com.ecit.shop.api;

import com.ecit.common.rx.EventBusRxVerticle;
import com.ecit.shop.constants.EventBusAddress;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class RedisVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LogManager.getLogger(RedisVerticle.class);
  private static final int MAX_RECONNECT_RETRIES = 16;

  private RedisOptions options = new RedisOptions();
  private Redis client;
  private RedisAPI redis;

  @Override
  public void start() {
    JsonObject redisConfig = this.config().getJsonObject("redis");
    options.setEndpoint(SocketAddress.inetSocketAddress(redisConfig.getInteger("port", 6379), redisConfig.getString("host", "localhost")))
            .setPassword(redisConfig.getString("password", "123456"));
    createRedisClient(onCreate -> {
      if (onCreate.succeeded()) {
        // connected to redis!
        client = onCreate.result();
        redis = RedisAPI.api(client);
      }
    });

    vertx.eventBus().consumer(EventBusAddress.GET_SESSION, this::getSessionHandler);
  }

  private void getSessionHandler(Message<JsonObject> message) {
    final JsonObject params = message.body();
    LOGGER.info("获取用户session信息：{}", params::encodePrettily);
  }

  /**
   * Will create a redis client and setup a reconnect handler when there is
   * an exception in the connection.
   */
  private void createRedisClient(Handler<AsyncResult<Redis>> handler) {
    Redis.createClient(vertx, options)
      .connect(onConnect -> {
        if (onConnect.succeeded()) {
          client = onConnect.result();
          // make sure the client is reconnected on error
          client.exceptionHandler(e -> {
            // attempt to reconnect
            attemptReconnect(0);
          });
        }
        // allow further processing
        handler.handle(onConnect);
      });
  }

  /**
   * Attempt to reconnect up to MAX_RECONNECT_RETRIES
   */
  private void attemptReconnect(int retry) {
    if (retry > MAX_RECONNECT_RETRIES) {
      // we should stop now, as there's nothing we can do.
    } else {
      // retry with backoff up to 1280ms
      long backoff = (long) (Math.pow(2, MAX_RECONNECT_RETRIES - Math.max(MAX_RECONNECT_RETRIES - retry, 9)) * 10);

      vertx.setTimer(backoff, timer -> createRedisClient(onReconnect -> {
        if (onReconnect.failed()) {
          attemptReconnect(retry + 1);
        }
      }));
    }
  }
}