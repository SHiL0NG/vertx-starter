package org.szhao.mongo;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.szhao.BootstrapVerticle;

/**
 * @author zsh
 * created 26/Aug/2020
 */
@RunWith(VertxUnitRunner.class)
public class MongoServiceTest {
    private Vertx vertx;

    @Before
    public void setUp(TestContext tc) {
        Async async = tc.async();
        vertx = Vertx.vertx();
        vertx.deployVerticle(BootstrapVerticle.class.getName(), ar -> {
            async.complete();
        });
    }

    @After
    public void tearDown(TestContext tc) {
        vertx.close(tc.asyncAssertSuccess());
    }

    @Test
    public void testMongoServiceProxy(TestContext tc) {
        Async async = tc.async();
        MongoService proxy = MongoService.createProxy(vertx);
        proxy.foobar("szhao", ar ->{
            tc.assertTrue(ar.succeeded());
            tc.assertEquals("szhao", ar.result().getString("userId"));
            async.complete();
        });
    }
}
