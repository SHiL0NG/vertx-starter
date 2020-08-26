package org.szhao.mongo;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zsh
 * created 26/Aug/2020
 */
public class MongoServiceVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MongoServiceVerticle.class);
    @Override
    public void start() throws Exception {
        // binding the proxy in a verticle to let the event handler run from a different execution context
        MongoService mongoService = MongoService.create(vertx);
        new ServiceBinder(vertx).setAddress(MongoService.address).register(MongoService.class, mongoService);
        LOG.info("binding event bus addresses ok");

        // retrieve mongo client configurations
        ConfigStoreOptions localJsonStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", "config.mongo.json"));

        ConfigRetriever retriever = ConfigRetriever.create(vertx,
                new ConfigRetrieverOptions().addStore(localJsonStore));

        retriever.getConfig(ar -> {
            if (ar.failed()) {
                LOG.info("config.mongo.json ko");
                return;
            }
            LOG.info("config.mongo.json: {}", ar.result().encode());
            MongoClient mongo = MongoClient.createShared(vertx, ar.result());
            // TODO: remove this test snippet
            JsonObject user = new JsonObject().put("username", "a.turing");
            mongo.insert("users", user, insertAR -> {
                if (insertAR.succeeded()) {
                    LOG.info("Mongo OK");
                } else {

                    LOG.info("Mongo KO: " + insertAR.cause().getMessage());
                }
            });

        });
    }
}
