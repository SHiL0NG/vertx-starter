package org.szhao;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.szhao.mongo.MongoServiceVerticle;

/**
 * @author zsh
 * created 26/Aug/2020
 */
public class BootstrapVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(BootstrapVerticle.class);

    @Override
    public void start() throws Exception {
        // create http server
        vertx.createHttpServer().requestHandler(req -> req.response().end("Hello"))
                .listen(9091);

        // deploy verticle
        vertx.deployVerticle(new MongoServiceVerticle());

        LOG.info("bootstrap started ok");
    }
}
