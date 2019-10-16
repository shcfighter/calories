//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ecit.auth.impl;

import com.ecit.auth.ShopUserSessionHandler;
import com.ecit.common.constants.Constants;
import com.ecit.common.db.JdbcRxRepositoryWrapper;
import com.ecit.common.result.ResultItems;
import com.ecit.shop.constants.UserSql;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ShopUserSessionHandlerImpl extends JdbcRxRepositoryWrapper implements ShopUserSessionHandler {

    private static final Logger LOGGER = LogManager.getLogger(ShopUserSessionHandlerImpl.class);

    public ShopUserSessionHandlerImpl(Vertx vertx, JsonObject config) {
        super(io.vertx.reactivex.core.Vertx.newInstance(vertx), config);
    }

    public void handle(RoutingContext routingContext) {
        String token = routingContext.request().getHeader("token");
        LOGGER.info("url: {}, token: {}", routingContext.request().uri(), token);
        //Future<JsonObject> future = this.getSession(token);
        Future<JsonObject> future = Future.future();
        redisClient.hget(Constants.VERTX_WEB_SESSION, token, handler -> {
            if (handler.succeeded()) {
                String user = handler.result();
                LOGGER.info("ShopUserSessionHandlerImpl redis user: {}", user);
                if (StringUtils.isEmpty(user)) {
                    this.retrieveOne(new JsonArray().add(token), UserSql.SELECT_BY_TOKEN_SQL)
                            .subscribe(future::complete, future::fail);
                    future.compose(u -> {
                        LOGGER.info("ShopUserSessionHandlerImpl db user: {}", u::encodePrettily);
                        this.setSession(token, u);
                        return Future.succeededFuture(u);
                    });
                }
                future.complete(new JsonObject(user));
            } else {
                LOGGER.info("ShopUserSessionHandlerImpl redis query token error:", handler.cause());
                this.retrieveOne(new JsonArray().add(token), UserSql.SELECT_BY_TOKEN_SQL)
                        .subscribe(future::complete, future::fail);
                future.compose(u -> {
                    this.setSession(token, u);
                    return Future.succeededFuture(u);
                });
            }
        });
        future.compose(user -> {
            LOGGER.info("session user: {}", user);
            if (Objects.isNull(user) || user.isEmpty()) {
                this.noAuth(routingContext);
                return Future.succeededFuture();
            }
            return Future.succeededFuture();
        });
        routingContext.next();

    }

    private void noAuth(RoutingContext routingContext){
        routingContext.response().setStatusCode(401)
                .putHeader("content-type", "application/json")
                .end(ResultItems.getEncodePrettily(ResultItems.getReturnItemsFailure("no_auth")));
		return ;
    }
}
