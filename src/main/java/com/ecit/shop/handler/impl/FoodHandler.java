package com.ecit.shop.handler.impl;

import com.ecit.common.constants.Constants;
import com.ecit.common.db.JdbcRxRepositoryWrapper;
import com.ecit.common.utils.RecommendFoodUtils;
import com.ecit.lucene.LuceneUtil;
import com.ecit.shop.constants.FoodSql;
import com.ecit.shop.handler.IFoodHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shwang on 2018/2/2.
 */
public class FoodHandler extends JdbcRxRepositoryWrapper implements IFoodHandler {

    private static final Logger LOGGER = LogManager.getLogger(FoodHandler.class);
    private static final String FOOD = "food";

    private final Vertx vertx;
    private final JsonObject config;
    private final LuceneUtil luceneUtil;

    public FoodHandler(Vertx vertx, JsonObject config) {
        super(vertx, config);
        this.vertx = vertx;
        this.config = config;
        this.luceneUtil = new LuceneUtil(Constants.LUCENE_INDICES + FOOD);
    }

    /**
     * 初始化food数据导入至lucene
     * @param handler
     * @return
     */
    @Override
    public IFoodHandler initLoadFood(Handler<AsyncResult<Void>> handler) {
        Future future = Future.future();
        if(config.getBoolean("loadFoodData", false)){
            this.luceneUtil.deleteAll();
            Future<JsonObject> rowFuture = Future.future();
            this.retrieveOne(new JsonArray().add(0), FoodSql.SELECT_FOOD_ROWNUM_SQL).subscribe(rowFuture::complete, rowFuture::fail);
            rowFuture.compose(rowJson -> {
                int row = rowJson.getInteger("row_num", 0);
                return CompositeFuture.all(this.createLuceneIndex(row));
            }).setHandler(compositeFutureAsyncResult -> {
                if (compositeFutureAsyncResult.failed()) {
                    LOGGER.error("构建lucene索引异常:{}", compositeFutureAsyncResult.cause());
                    future.fail(compositeFutureAsyncResult.cause());
                } else {
                    for (Object o : compositeFutureAsyncResult.result().list()) {
                        luceneUtil.createIndex((List<JsonObject>) o, true);
                    }
                    future.complete();
                }
            });
        } else {
            future.complete();
        }
        future.setHandler(handler);
        return this;
    }

    /**
     * 批量插入lucene
     * @param row
     * @return
     */
    private List<Future> createLuceneIndex(int row){
        final int size = 100;
        int totalPage = (row % size != 0) ? (row / size + 1) : (row / size);
        final List<Future> isOk = new ArrayList<>(totalPage);
        for (int i = 1; i <= totalPage; i++) {
            Future<List<JsonObject>> foodFuture = Future.future();
            isOk.add(foodFuture);
            this.retrieveByPage(new JsonArray().add(0),size, i, FoodSql.SELECT_FOOD_SQL).subscribe(foodFuture::complete, foodFuture::fail);
        }
        return isOk;
    }

    @Override
    public IFoodHandler searchFood(String keyword, int pageSize, int curPage, Handler<AsyncResult<JsonObject>> handler) {
        Future<JsonObject> future = Future.future();
        try {
            future.complete(luceneUtil.searchPage(new String[]{"name"}, RecommendFoodUtils.randomFoodName(keyword), pageSize, curPage));
        } catch (Exception e) {
            future.fail(e);
        }
        future.setHandler(handler);
        return this;
    }

    @Override
    public IFoodHandler findFoodById(long id, Handler<AsyncResult<JsonObject>> handler) {
        Future<JsonObject> future = Future.future();
        this.retrieveOne(new JsonArray().add(id).add(0), FoodSql.SELECT_FOOD_BY_ID_SQL).subscribe(future::complete, future::fail);
        future.setHandler(handler);
        return this;
    }

}
