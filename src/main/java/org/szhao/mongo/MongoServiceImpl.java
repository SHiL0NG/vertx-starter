package org.szhao.mongo;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zsh
 * created 26/Aug/2020
 */
public class MongoServiceImpl implements MongoService {
    private static final Logger LOG = LoggerFactory.getLogger(MongoServiceImpl.class);
    private final Vertx vertx;

    public MongoServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void foobar(String userId, Handler<AsyncResult<JsonObject>> handler) {
        LOG.info("processing request for {}", userId);
        handler.handle(Future.succeededFuture(new JsonObject().put("userId", userId)));
    }
}
