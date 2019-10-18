package com.ecit.shop.api;

import com.ecit.common.constants.Constants;
import com.ecit.common.enums.EventBusStatus;
import com.ecit.common.rx.EventBusRxVerticle;
import com.ecit.shop.constants.EventBusAddress;
import com.google.common.collect.Lists;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.redis.client.Redis;
import io.vertx.reactivex.redis.client.RedisAPI;
import io.vertx.reactivex.redis.client.Response;
import io.vertx.redis.client.RedisOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RedisRxVerticle extends EventBusRxVerticle {

  private static final Logger LOGGER = LogManager.getLogger(RedisRxVerticle.class);
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
    vertx.eventBus().consumer(EventBusAddress.SET_SESSION, this::setSessionHandler);
  }

  private void getSessionHandler(Message<JsonObject> message) {
    final JsonObject params = message.body();
    LOGGER.info("获取用户session信息：{}", params::encodePrettily);
    /*redis.rxHget(Constants.VERTX_WEB_SESSION, params.getString(Constants.TOKEN)).subscribe(response -> {
      message.reply(this.resultEventBus(EventBusStatus.SUCCESS, response.toString()));
    }, fail -> {
      System.out.println("00000000000000");
    });*/

    redis.hget(Constants.VERTX_WEB_SESSION, params.getString(Constants.TOKEN), response -> {
      if (response.succeeded()) {
        Response userResponse = response.result();
        message.reply(this.resultEventBus(EventBusStatus.SUCCESS, Objects.isNull(userResponse) ? null : new JsonObject(userResponse.toString())));
        return;
      } else {
        System.out.println("00000000000000");
      }
    });
  }

  private void setSessionHandler(Message<JsonObject> message) {
    final JsonObject params = message.body();
    LOGGER.info("设置用户session信息：{}", params::encodePrettily);
    List<String> valueList = Lists.newArrayList(Constants.VERTX_WEB_SESSION, params.getString(Constants.TOKEN), params.getJsonObject("user").toString());
    redis.rxHset(valueList).subscribe();
    redis.rxExpire(Constants.VERTX_WEB_SESSION, Constants.SESSION_EXPIRE_TIME).subscribe();
    message.reply(this.resultEventBus(EventBusStatus.SUCCESS, null));
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