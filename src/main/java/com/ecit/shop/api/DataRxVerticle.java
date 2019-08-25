package com.ecit.shop.api;

import com.ecit.common.constants.Constants;
import com.ecit.common.enums.EventBusStatus;
import com.ecit.common.rx.EventBusRxVerticle;
import com.ecit.shop.constants.EventBusAddress;
import com.ecit.shop.handler.IFoodHandler;
import com.ecit.shop.handler.IHeatHandler;
import com.ecit.shop.handler.IUserHandler;
import com.ecit.shop.handler.impl.FoodHandler;
import com.ecit.shop.handler.impl.HeatHandler;
import com.ecit.shop.handler.impl.UserHandler;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by shwang on 2018/2/2.
 */
public class DataRxVerticle extends EventBusRxVerticle {

    private static final Logger LOGGER = LogManager.getLogger(DataRxVerticle.class);
    private IUserHandler userHandler;
    private IFoodHandler foodHandler;
    private IHeatHandler heatHandler;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        this.userHandler = new UserHandler(vertx, this.config());
        this.foodHandler = new FoodHandler(vertx, this.config());
        this.heatHandler = new HeatHandler(vertx, this.config());

        vertx.eventBus().consumer(EventBusAddress.ACCREDIT, this::accreditHandler);
        vertx.eventBus().consumer(EventBusAddress.USER_CHECK, this::checkTokenHandler);
        vertx.eventBus().consumer(EventBusAddress.SEARCH_FOOD, this::searchHandler);
        vertx.eventBus().consumer(EventBusAddress.FOOD_DETAIL, this::findFoodByIdHandler);
        vertx.eventBus().consumer(EventBusAddress.HEAT_DETAIL, this::findHeatByFoodIdHandler);
        vertx.eventBus().consumer(EventBusAddress.GET_USER, this::getUserInfoHandler);
        vertx.eventBus().consumer(EventBusAddress.UPDATE_USER, this::updateUserInfoHandler);
        vertx.eventBus().consumer(EventBusAddress.INIT_FOOD, this::initFoodHandler);
    }

    /**
     * 授权登录
     * @param message
     */
    private void accreditHandler(Message<JsonObject> message){
        final JsonObject params = message.body();
        LOGGER.info("授权用户信息：{}", params::encodePrettily);
        userHandler.accredit(params.getString("code"), params.getJsonObject("user_info"), handler -> {
            if(handler.failed()){
                LOGGER.info("授权结果：", handler.cause());
                message.fail(500, handler.cause().getMessage());
                return ;
            }
            JsonObject result = handler.result();
            message.reply(this.resultEventBus(EventBusStatus.SUCCESS,result));
            return ;
        });
    }

    /**
     * 检查token是否正确
     * @param message
     */
    private void checkTokenHandler(Message<JsonObject> message){
        final String token = message.body().getString(Constants.TOKEN);
        if(StringUtils.isEmpty(token)){
            LOGGER.info("检查token为空");
            message.reply(this.resultEventBus(EventBusStatus.NO_DATA,"授权失败"));
            return;
        }
        userHandler.checkToken(token, handler -> {
            if(handler.failed() || !handler.result()){
                LOGGER.info("token【{}】授权失败", token, handler.cause());
                message.fail(500, handler.cause().getMessage());
                return;
            }
            message.reply(this.resultEventBus(EventBusStatus.SUCCESS,"授权成功"));
            return;
        });
    }

    /**
     * 初始化食物信息
     */
    private void initFoodHandler(Message<JsonObject> message){
        foodHandler.initLoadFood(handler -> {
            if(handler.failed()){
                LOGGER.info("获取商品类别信息失败:", handler.cause());
                message.fail(500, handler.cause().getMessage());
                return;
            }
            message.reply(this.resultEventBus(EventBusStatus.SUCCESS, handler.result()));
            return;
        });
    }

    /**
     * 搜索食物信息
     * @param message 消息体
     */
    private void searchHandler(Message<JsonObject> message){
        final JsonObject params = message.body();
        final String keyword = params.getString("keyword");
        final int page = Optional.ofNullable(params.getInteger("curPage")).orElse(1);
        long start = System.currentTimeMillis();
        foodHandler.searchFood(keyword, Optional.ofNullable(params.getInteger("pageSize")).orElse(12), page, handler -> {
            LOGGER.info("查询食物结束线程：{}, search time:{}", Thread.currentThread().getName(), System.currentTimeMillis() - start);
            if(handler.failed()){
                LOGGER.info("搜索食物异常：", handler.cause());
                message.fail(500, handler.cause().getMessage());
                return ;
            }
            if(Objects.isNull(handler.result())){
                message.reply(this.resultEventBus(EventBusStatus.NO_DATA,"暂无该食物！"));
                return ;
            }
            final JsonObject result = handler.result().put("page", page);
            message.reply(this.resultEventBus(EventBusStatus.SUCCESS,result));
            return ;
        });
    }

    /**
     * 根据food id查询详情信息
     * @param message 消息体
     */
    private void findFoodByIdHandler(Message<JsonObject> message){
        final JsonObject params = message.body();
        foodHandler.findFoodById(params.getLong(Constants.ID), handler -> {
            if (handler.failed()) {
                LOGGER.info("根据id查询食物失败：", handler.cause());
                message.fail(500, handler.cause().getMessage());
                return ;
            } else {
                message.reply(this.resultEventBus(EventBusStatus.SUCCESS, handler.result()));
                return ;
            }
        });
    }

    /**
     * 查询热量信息
     * @param message
     */
    private void findHeatByFoodIdHandler(Message<JsonObject> message){
        final JsonObject params = message.body();
        heatHandler.findHeatByFoodId(params.getLong(Constants.ID), handler -> {
            if (handler.failed()) {
                LOGGER.info("根据id查询热量失败：", handler.cause());
                message.fail(500, handler.cause().getMessage());
                return ;
            } else {
                message.reply(this.resultEventBus(EventBusStatus.SUCCESS, handler.result()));
                return ;
            }
        });
    }

    /**
     * 获取用户信息
     * @param message
     */
    private void getUserInfoHandler(Message<JsonObject> message){
        final String token = message.body().getString(Constants.TOKEN);
        userHandler.getUserInfo(token, handler -> {
            if (handler.failed()) {
                LOGGER.info("获取用户信息失败：", handler.cause());
                message.fail(500, handler.cause().getMessage());
                return ;
            } else {
                message.reply(this.resultEventBus(EventBusStatus.SUCCESS, handler.result()));
                return ;
            }
        });
    }

    /**
     * 更改手机号码
     * @param message
     */
    private void updateUserInfoHandler(Message<JsonObject> message){
        JsonObject params = message.body();
        final String token = params.getString(Constants.TOKEN);
        userHandler.updateUserInfo(token, params, handler -> {
            if(handler.failed() || handler.result().getUpdated() <= 0){
                LOGGER.info("更改手机号码失败：", handler.cause());
                message.fail(500, handler.cause().getMessage());
                return;
            }
            message.reply(this.resultEventBus(EventBusStatus.SUCCESS, "更改手机号码成功"));
            return ;
        });
    }
}
