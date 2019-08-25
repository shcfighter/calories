package com.ecit;

import com.ecit.common.rx.BaseMicroserviceRxVerticle;
import com.ecit.shop.api.DataRxVerticle;
import com.ecit.shop.api.RestCaloriesRxVerticle;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.reactivex.core.Vertx;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * Created by shwang on 2018/2/2.
 */
public class CaloriesVerticle extends BaseMicroserviceRxVerticle{

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        vertx.getDelegate().deployVerticle(RestCaloriesRxVerticle.class, new DeploymentOptions().setConfig(this.config()).setInstances(this.config().getInteger("instances", 1)));
        vertx.getDelegate().deployVerticle(DataRxVerticle.class, new DeploymentOptions().setConfig(this.config()).setInstances(this.config().getInteger("instances", 2)));
    }

    public static void main(String[] args) {
        Config cfg = new Config();
        GroupConfig group = new GroupConfig();
        group.setName("p-dev");
        group.setPassword("p-dev");
        cfg.setGroupConfig(group);
        // 申明集群管理器
        ClusterManager mgr = new HazelcastClusterManager(cfg);
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.rxClusteredVertx(options).subscribe(v -> v.deployVerticle(CaloriesVerticle.class.getName(),
                new DeploymentOptions().setConfig(new JsonObject()
                        .put("instances", 1)
                        .put("loadFoodData", true)
                        .put("postgresql", new JsonObject().put("host", "111.231.132.168")
                                .put("port", 5432)
                                .put("maxPoolSize", 50)
                                .put("username", "postgres")
                                .put("password", "h123456")
                                .put("database", "calories")
                                .put("charset", "UTF-8")
                                .put("queryTimeout", 10000))
                        .put("weixin.appid", "wxf98bdcd6150b7e33")
                        .put("weixin.secret", "35b319c6a9aee16e3977bf0d0d24ff7e")
                        .put("redis", new JsonObject().put("host", "111.231.132.168")
                                .put("port", 6379)
                                .put("password", "h123456")
                        )
                )));
    }
}
