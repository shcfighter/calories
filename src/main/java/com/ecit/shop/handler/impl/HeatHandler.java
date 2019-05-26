package com.ecit.shop.handler.impl;

import com.ecit.common.db.JdbcRxRepositoryWrapper;
import com.ecit.shop.constants.HeatSql;
import com.ecit.shop.handler.IHeatHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by shwang on 2018/2/2.
 */
public class HeatHandler extends JdbcRxRepositoryWrapper implements IHeatHandler {

    private static final Logger LOGGER = LogManager.getLogger(HeatHandler.class);

    final Vertx vertx;
    final JsonObject config;

    public HeatHandler(Vertx vertx, JsonObject config) {
        super(vertx, config);
        this.vertx = vertx;
        this.config = config;
    }


    @Override
    public IHeatHandler findHeatByFoodId(long foodId, Handler<AsyncResult<List<JsonObject>>> handler) {
        Future<List<JsonObject>> future = Future.future();
        this.retrieveMany(new JsonArray().add(foodId).add(0), HeatSql.SELECT_FOOD_BY_FOODID_SQL).subscribe(future::complete, future::fail);
        future.setHandler(handler);
        return this;
    }

}
