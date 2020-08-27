package org.szhao;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.szhao.api.HttpVerticle;
import org.szhao.mongo.MongoServiceVerticle;

/**
 * @author zsh
 * created 26/Aug/2020
 */
public class BootstrapVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(BootstrapVerticle.class);

    @Override
    public void start(Promise<Void> promise) throws Exception {

        // retrieve configurations
        ConfigStoreOptions localJsonStore = new ConfigStoreOptions()
                .setType("file").setConfig(new JsonObject().put("path", "config.json"));

        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(localJsonStore));

        retriever.getConfig(ar -> {
            if (ar.failed()) {
                promise.fail("");
                LOG.info("config.json ko");
                return;
            }
            LOG.debug("config.json: {}", ar.result().encode());
            DeploymentOptions mongoOptions = new DeploymentOptions().setConfig(ar.result().getJsonObject("mongo"));
            vertx.deployVerticle(new MongoServiceVerticle(),
                    mongoOptions);
            DeploymentOptions httpOptions = new DeploymentOptions().setConfig(ar.result().getJsonObject("http"));
            vertx.deployVerticle(new HttpVerticle(),
                    httpOptions);
            deployVerticle(new MongoServiceVerticle(), mongoOptions)
                    .compose(s -> deployVerticle(new HttpVerticle(), httpOptions))
                    .onComplete(deployAR -> {
                        if (deployAR.succeeded()) promise.complete();
                        else promise.fail(deployAR.cause());
                    });
        });
    }

    private Future<String> deployVerticle(Verticle verticle, DeploymentOptions options) {
        Promise<String> promise = Promise.promise();
        vertx.deployVerticle(verticle, options, ar -> {
            if (ar.failed()) promise.fail(ar.cause());
            else promise.complete(ar.result());
        });
        return promise.future();
    }
}
