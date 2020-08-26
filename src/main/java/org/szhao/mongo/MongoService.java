package org.szhao.mongo;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author zsh
 * created 26/Aug/2020
 */
@ProxyGen
public interface MongoService {
    String address = "mongo.service";

    static MongoService create(Vertx vertx) {
        return new MongoServiceImpl(vertx);
    }

    static MongoService createProxy(Vertx vertx) {
        return new MongoServiceVertxEBProxy(vertx, MongoService.address);
    }

    void foobar(String userId, Handler<AsyncResult<JsonObject>> handler);
}
