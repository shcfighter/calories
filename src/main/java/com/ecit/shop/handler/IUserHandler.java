package com.ecit.shop.handler;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;

/**
 * Created by shwang on 2018/2/2.
 */
@ProxyGen
@VertxGen
public interface IUserHandler {

    @Fluent
    IUserHandler accredit(String code, JsonObject userInfo, Handler<AsyncResult<JsonObject>> handler);

    @Fluent
    IUserHandler checkToken(String token, Handler<AsyncResult<Boolean>> handler);

    @Fluent
    IUserHandler updateUserInfo(String token, JsonObject params, Handler<AsyncResult<UpdateResult>> handler);

    @Fluent
    IUserHandler getUserInfo(String token, Handler<AsyncResult<JsonObject>> handler);
}
