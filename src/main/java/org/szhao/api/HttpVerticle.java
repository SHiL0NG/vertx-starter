package org.szhao.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

/**
 * @author zsh
 * created 27/Aug/2020
 */
public class HttpVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(HttpVerticle.class);

    private static final Handler<RoutingContext> ERROR_HANDLER = rx -> {
        JsonObject errorObject = new JsonObject().put("code", 400)
                .put("message", rx.failure() != null ? rx.failure().getMessage() : "Validation Exception");
        rx.response().setStatusCode(400)
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(errorObject.encode());
    };

    private static final Handler<RoutingContext> INTERNAL_ERROR_HANDLER = rx -> {
        JsonObject err = new JsonObject().put("code", 500)
                .put("message", "Internal server error, check with admin");
        rx.response().setStatusCode(500)
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(err.encode());
    };

    @Override
    public void start(Promise<Void> promise) throws Exception {
        OpenAPI3RouterFactory.create(vertx, "api.yaml", ar -> {
            if (ar.failed()) {
                LOG.error("http verticle start up KO");
                promise.fail("");
                return;
            }
            OpenAPI3RouterFactory factory = ar.result();

            // correspondence with yaml file
            factory.addHandlerByOperationId("getHome", rx -> rx.response().end("Welcome"));

            Router router = factory.getRouter();
            // global ERROR handlers
            router.errorHandler(400, ERROR_HANDLER);
            router.errorHandler(INTERNAL_SERVER_ERROR.code(), INTERNAL_ERROR_HANDLER);

            Integer port = config().getInteger("port", 8809);
            String host = config().getString("host", "localhost");

            LOG.debug("starting http server at {}:{}", host, port);
            vertx.createHttpServer().requestHandler(router)
                    .listen(port, host, http -> {
                        promise.complete();
                        LOG.info("http verticle start up OK");
                    });
        });
    }
}
