package com.ecit.shop.api;

import com.ecit.auth.ShopUserSessionHandler;
import com.ecit.common.rx.RestAPIRxVerticle;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;

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
        //router.get("/calories/api/init").blockingHandler(this::initFoodHandler);     //获取category信息
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
        userHandler.accredit(params.getString("code"), params.getJsonObject("user_info"), handler -> {
            if(handler.failed()){
                LOGGER.info("授权结果：", handler.cause());
                this.returnWithFailureMessage(context, "授权失败");
                return ;
            }
            JsonObject result = handler.result();
            this.returnWithSuccessMessage(context, "授权成功", result);
            return ;
        });
    }

    /**
     * 检查token是否正确
     * @param context
     */
    private void checkTokenHandler(RoutingContext context){
        final String token = context.request().getHeader("token");
        if(StringUtils.isEmpty(token)){
            LOGGER.info("检查token为空");
            this.returnWithFailureMessage(context, "授权失败");
            return;
        }
        userHandler.checkToken(token, handler -> {
            if(handler.failed() || !handler.result()){
                LOGGER.info("token【{}】授权失败", token, handler.cause());
                this.returnWithFailureMessage(context, "授权失败");
                return;
            }
            this.returnWithSuccessMessage(context, "授权成功");
            return;
        });
    }

    /**
     * 初始化食物信息
     */
    private void initFoodHandler(RoutingContext context){
        foodHandler.initLoadFood(hander -> {
            if(hander.failed()){
                LOGGER.info("获取商品类别信息失败:", hander.cause());
                this.returnWithFailureMessage(context, "获取商品类别信息失败");
                return;
            }
            this.returnWithSuccessMessage(context, "获取商品类别信息成功", hander.result());
            return;
        });
    }

    /**
     * 搜索食物信息
     * @param context
     */
    private void searchHandler(RoutingContext context){
        final JsonObject params = context.getBodyAsJson();
        final String keyword = params.getString("keyword");
        final int page = Optional.ofNullable(params.getInteger("curPage")).orElse(1);
        long start = System.currentTimeMillis();
        foodHandler.searchFood(keyword, Optional.ofNullable(params.getInteger("pageSize")).orElse(12), page, handler -> {
            LOGGER.info("查询食物结束线程：{}, search time:{}", Thread.currentThread().getName(), System.currentTimeMillis() - start);
            if(handler.failed()){
                LOGGER.info("搜索食物异常：", handler.cause());
                this.returnWithFailureMessage(context, "暂无该食物！");
                return ;
            }
            if(Objects.isNull(handler.result())){
                this.returnWithFailureMessage(context, "暂无该食物！");
                return ;
            }
            final JsonObject result = handler.result();
            this.returnWithSuccessMessage(context, "查询成功", result.getLong("total").intValue(),
                    result.getJsonArray("hits"), page);
            return ;
        });
    }

    /**
     * 根据food id查询详情信息
     * @param context
     */
    private void findFoodByIdHandler(RoutingContext context){
        foodHandler.findFoodById(Long.parseLong(context.request().getParam("id")), handler -> {
            if (handler.failed()) {
                LOGGER.info("根据id查询食物失败：", handler.cause());
                this.returnWithFailureMessage(context, "查询食物失败");
                return ;
            } else {
                this.returnWithSuccessMessage(context, "查询食物信息详情成功", handler.result());
                return ;
            }
        });
    }

    /**
     * 查询热量信息
     * @param context
     */
    private void findHeatByFoodIdHandler(RoutingContext context){
        heatHandler.findHeatByFoodId(Long.parseLong(context.request().getParam("id")), handler -> {
            if (handler.failed()) {
                LOGGER.info("根据id查询热量失败：", handler.cause());
                this.returnWithFailureMessage(context, "查询热量失败");
                return ;
            } else {
                this.returnWithSuccessMessage(context, "查询热量信息详情成功", handler.result());
                return ;
            }
        });
    }

    /**
     * 获取用户信息
     * @param context
     */
    private void getUserInfoHandler(RoutingContext context){
        userHandler.getUserInfo(context.request().getHeader("token"), handler -> {
            if (handler.failed()) {
                LOGGER.info("获取用户信息失败：", handler.cause());
                this.returnWithFailureMessage(context, "获取用户信息失败");
                return ;
            } else {
                this.returnWithSuccessMessage(context, "获取用户信息失败成功", handler.result());
                return ;
            }
        });
    }

    /**
     * 更改手机号码
     * @param context
     */
    private void updateUserInfoHandler(RoutingContext context){
        JsonObject params = context.getBodyAsJson();
        userHandler.updateUserInfo(context.request().getHeader("token"), params, hander -> {
            if(hander.failed() || hander.result().getUpdated() <= 0){
                LOGGER.info("更改手机号码失败：", hander.cause());
                this.returnWithFailureMessage(context, "更改手机号码失败");
                return;
            }
            this.returnWithSuccessMessage(context, "更改手机号码成功");
            return ;
        });
    }
}
