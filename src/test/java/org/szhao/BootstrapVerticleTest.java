package org.szhao;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zsh
 * created 26/Aug/2020
 */
@RunWith(VertxUnitRunner.class)
public class BootstrapVerticleTest {
    private static final Logger LOG = LoggerFactory.getLogger(BootstrapVerticleTest.class);
    private Vertx vertx;

    @Before
    public void setUp(TestContext tc) {
        Async async = tc.async();
        vertx = Vertx.vertx();
        vertx.deployVerticle(BootstrapVerticle.class.getName(), ar -> {
            LOG.debug("deploy bootstrap verticle OK");
            async.complete();
        });
    }

    @After
    public void tearDown(TestContext tc) {
        vertx.close(tc.asyncAssertSuccess());
    }

    @Test
    public void testServerStarted(TestContext tc) {
        Async async = tc.async();
        WebClient client = WebClient.create(vertx);
        client.get(9091, "localhost", "/").send(ar -> {
            tc.assertTrue(ar.succeeded());
            tc.assertEquals(200, ar.result().statusCode());
            LOG.info("get result {}", ar.result().body());
            async.complete();
        });
    }

}
