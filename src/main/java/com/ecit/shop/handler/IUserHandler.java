package com.ecit.shop.handler;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

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
    IUserHandler updateMobile(String token, JsonObject params, Handler<AsyncResult<Integer>> handler);
}
