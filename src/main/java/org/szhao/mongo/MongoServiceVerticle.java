package org.szhao.mongo;

import io.vertx.core.AbstractVerticle;
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
        LOG.info("mongo verticle start up OK");
    }
}
