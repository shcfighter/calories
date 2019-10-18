//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ecit.auth.impl;

import com.ecit.auth.ShopUserSessionHandler;
import com.ecit.common.constants.Constants;
import com.ecit.common.db.JdbcRxRepositoryWrapper;
import com.ecit.common.result.ResultItems;
import com.ecit.common.utils.JsonUtils;
import com.ecit.shop.constants.EventBusAddress;
import com.ecit.shop.constants.UserSql;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ShopUserSessionHandlerImpl extends JdbcRxRepositoryWrapper implements ShopUserSessionHandler {

    private static final Logger LOGGER = LogManager.getLogger(ShopUserSessionHandlerImpl.class);
    private static io.vertx.reactivex.core.Vertx _vertx;

    public ShopUserSessionHandlerImpl(Vertx vertx, JsonObject config) {
        super(io.vertx.reactivex.core.Vertx.newInstance(vertx), config);
        _vertx = io.vertx.reactivex.core.Vertx.newInstance(vertx);
    }

    public void handle(RoutingContext routingContext) {
        String token = routingContext.request().getHeader(Constants.TOKEN);
        LOGGER.info("url: {}, token: {}", routingContext.request().uri(), token);
        _vertx.eventBus().rxSend(EventBusAddress.GET_SESSION, new JsonObject().put(Constants.TOKEN, token)).subscribe(message -> {
            JsonObject user = JsonObject.mapFrom(message.body()).getJsonObject(Constants.BODY);
            LOGGER.info("session user: {}", user);
            if (Objects.isNull(user) || user.isEmpty()) {
                this.retrieveOne(new JsonArray().add(token), UserSql.SELECT_BY_TOKEN_SQL)
                        .subscribe(u -> {
                            LOGGER.info("getUserInfo db user: {}", u::encodePrettily);
                            if (Objects.isNull(u)) {
                                this.noAuth(routingContext);
                                return ;
                            }
                            _vertx.eventBus().rxSend(EventBusAddress.SET_SESSION, new JsonObject().put(Constants.TOKEN, token).put("user", u)).subscribe();
                        }, fail -> {
                            this.noAuth(routingContext);
                            return ;
                        });
            }
            routingContext.next();
        }, fail -> {
            LOGGER.info("获取session失败：", fail.getCause());
            this.noAuth(routingContext);
        });

    }

    private void noAuth(RoutingContext routingContext){
        routingContext.response().setStatusCode(401)
                .putHeader("content-type", "application/json")
                .end(ResultItems.getEncodePrettily(ResultItems.getReturnItemsFailure("no_auth")));
		return ;
    }
}
