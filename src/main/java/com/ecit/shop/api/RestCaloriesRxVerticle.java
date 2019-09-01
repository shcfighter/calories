package com.ecit.shop.api;

import com.ecit.auth.ShopUserSessionHandler;
import com.ecit.common.constants.Constants;
import com.ecit.common.rx.RestAPIRxVerticle;
import com.ecit.common.utils.IpUtils;
import com.ecit.shop.constants.EventBusAddress;
import com.ecit.shop.handler.IFoodHandler;
import com.ecit.shop.handler.IHeatHandler;
import com.ecit.shop.handler.IUserHandler;
import com.ecit.shop.handler.impl.FoodHandler;
import com.ecit.shop.handler.impl.HeatHandler;
import com.ecit.shop.handler.impl.UserHandler;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by shwang on 2018/2/2.
 */
public class RestCaloriesRxVerticle extends RestAPIRxVerticle{

    private static final Logger LOGGER = LogManager.getLogger(RestCaloriesRxVerticle.class);
    private IUserHandler userHandler;
    private IFoodHandler foodHandler;
    private IHeatHandler heatHandler;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        this.userHandler = new UserHandler(vertx, this.config());
        this.foodHandler = new FoodHandler(vertx, this.config());
        this.heatHandler = new HeatHandler(vertx, this.config());

        //init food data to lucene
        //this.foodHandler.initLoadFood(handler -> {});

        final Router router = Router.router(vertx);
        // cookie and session handler
        this.enableLocalSession(router, "calories_session");
        this.enableCorsSupport(router);
        // body handler
        router.route().handler(BodyHandler.create());
        //不需要登录
        router.put("/calories/api/accredit").handler(this::accreditHandler);     //微信授权
        router.get("/calories/api/user/check").handler(this::checkTokenHandler);     //检查校验token
        router.get("/calories/api/init").handler(this::initFoodHandler);     //获取category信息
        router.post("/calories/api/food/search").handler(this::searchHandler);     //搜索食物信息
        router.get("/calories/api/food/detail/:id").handler(this::findFoodByIdHandler);     //搜索食物详情信息
        router.get("/calories/api/heat/:id").handler(this::findHeatByFoodIdHandler);     //搜索热量详情信息

        router.getDelegate().route().handler(ShopUserSessionHandler.create(vertx.getDelegate(), this.config()));

        // API route handler    需要登录
        router.get("/calories/api/user/get").handler(this::getUserInfoHandler);      //获取用户信息
        router.put("/calories/api/user/update").handler(this::updateUserInfoHandler);      //更改手机号码

        //全局异常处理
        this.globalVerticle(router);

        // get HTTP host and port from configuration, or use default value
        String host = config().getString("user.http.address", "localhost");
        int port = config().getInteger("user.http.port", 8080);

        // create HTTP server and publish REST handler
        createHttpServer(router, host, port).subscribe(server -> {
            LOGGER.info("calories server started!");
        }, error -> {
            LOGGER.info("calories server start fail!", error);
        });
    }

    /**
     * 授权登录
     * @param context
     */
    private void accreditHandler(RoutingContext context){
        final JsonObject params = context.getBodyAsJson();
        LOGGER.info("授权用户信息：{}", params::encodePrettily);
        vertx.eventBus().rxSend(EventBusAddress.ACCREDIT, params).subscribe(message -> {
            this.returnWithSuccessMessage(context, "授权成功", JsonObject.mapFrom(message.body()).getJsonObject(Constants.BODY));
            return ;
        }, fail -> {
            LOGGER.info("授权结果：", fail.getCause());
            this.returnWithFailureMessage(context, "授权失败");
            return;
        });
    }

    /**
     * 检查token是否正确
     * @param context
     */
    private void checkTokenHandler(RoutingContext context){
        final String token = context.request().getHeader("token");
        JsonObject params = new JsonObject().put(Constants.TOKEN, token);
        vertx.eventBus().rxSend(EventBusAddress.USER_CHECK, params).subscribe(message -> {
            JsonObject result = JsonObject.mapFrom(message.body());
            if (this.isNoDataResult(result)) {
                this.returnWithFailureMessage(context, "授权失败");
                return;
            }
            this.returnWithSuccessMessage(context, "授权成功", result.getJsonObject(Constants.BODY));
            return ;
        }, fail -> {
            LOGGER.info("授权结果：", fail.getCause());
            this.returnWithFailureMessage(context, "授权失败");
            return;
        });
    }

    /**
     * 初始化食物信息
     */
    private void initFoodHandler(RoutingContext context){
        vertx.eventBus().rxSend(EventBusAddress.INIT_FOOD, new JsonObject()).subscribe(message -> {
            this.returnWithSuccessMessage(context, "获取商品类别信息成功", JsonObject.mapFrom(message.body()).getJsonObject(Constants.BODY));
            return ;
        }, fail -> {
            LOGGER.info("获取商品类别信息失败：", fail.getCause());
            this.returnWithFailureMessage(context, "获取商品类别信息失败");
            return;
        });
    }

    /**
     * 搜索食物信息
     * @param context
     */
    private void searchHandler(RoutingContext context){
        final JsonObject params = context.getBodyAsJson();
        LOGGER.info("{} 查询食物：{}", IpUtils.getIpAddr(context.request().getDelegate()), params.getString("keyword") );
        vertx.eventBus().rxSend(EventBusAddress.SEARCH_FOOD, params).subscribe(message -> {
            final JsonObject result = JsonObject.mapFrom(message.body());
            if(this.isNoDataResult(result)){
                this.returnWithFailureMessage(context, result.getString(Constants.BODY));
                return ;
            }
            JsonObject body = result.getJsonObject(Constants.BODY);
            this.returnWithSuccessMessage(context, "查询成功", body.getLong("total").intValue(),
                    body.getJsonArray("hits"), body.getInteger("page", 1));
            return ;
        }, fail -> {
            LOGGER.info("搜索食物异常：", fail.getCause());
            this.returnWithFailureMessage(context, "暂无该食物！");
            return ;
        });
    }

    /**
     * 根据food id查询详情信息
     * @param context
     */
    private void findFoodByIdHandler(RoutingContext context){
        JsonObject params = new JsonObject().put(Constants.ID, Long.parseLong(context.request().getParam(Constants.ID)));
        vertx.eventBus().rxSend(EventBusAddress.FOOD_DETAIL, params).subscribe(message -> {
            this.returnWithSuccessMessage(context, "查询食物信息详情成功", JsonObject.mapFrom(message.body()).getJsonObject(Constants.BODY));
            return ;
        }, fail -> {
            LOGGER.info("根据id查询食物失败：", fail.getCause());
            this.returnWithFailureMessage(context, "查询食物失败");
            return;
        });
    }

    /**
     * 查询热量信息
     * @param context
     */
    private void findHeatByFoodIdHandler(RoutingContext context){
        JsonObject params = new JsonObject().put(Constants.ID, Long.parseLong(context.request().getParam(Constants.ID)));
        vertx.eventBus().rxSend(EventBusAddress.HEAT_DETAIL, params).subscribe(message -> {
            this.returnWithSuccessMessage(context, "查询热量信息详情成功", JsonObject.mapFrom(message.body()).getJsonArray(Constants.BODY));
            return ;
        }, fail -> {
            LOGGER.info("根据id查询热量失败：", fail.getCause());
            this.returnWithFailureMessage(context, "查询热量失败");
            return;
        });
    }

    /**
     * 获取用户信息
     * @param context
     */
    private void getUserInfoHandler(RoutingContext context){
        LOGGER.info("header token:{}", context.request().getHeader(Constants.TOKEN));
        JsonObject params = new JsonObject().put(Constants.TOKEN, context.request().getHeader(Constants.TOKEN));
        vertx.eventBus().rxSend(EventBusAddress.GET_USER, params).subscribe(message -> {
            this.returnWithSuccessMessage(context, "获取用户信息成功", JsonObject.mapFrom(message.body()).getJsonObject(Constants.BODY));
            return ;
        }, fail -> {
            LOGGER.info("获取用户信息失败：", fail.getCause());
            this.returnWithFailureMessage(context, "获取用户信息失败");
            return;
        });
    }

    /**
     * 更改手机号码
     * @param context
     */
    private void updateUserInfoHandler(RoutingContext context){
        JsonObject params = context.getBodyAsJson().put(Constants.TOKEN, context.request().getHeader(Constants.TOKEN));
        vertx.eventBus().rxSend(EventBusAddress.UPDATE_USER, params).subscribe(message -> {
            this.returnWithSuccessMessage(context, "更改手机号码成功");
            return ;
        }, fail -> {
            LOGGER.info("更改手机号码失败：", fail.getCause());
            this.returnWithFailureMessage(context, "更改手机号码失败");
            return;
        });
    }
}
