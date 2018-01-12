package com.valsesia.test.testng;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MainVerticle extends AbstractVerticle {


    @Override
    public void start(Future<Void> startFuture) throws Exception {

        // Now deploy some other verticle:

        vertx.deployVerticle("com.valsesia.test.testng.HttpServerVerticle", res -> {
            if (res.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(res.cause());
            }
        });
        
    }

}
