package com.valsesia.test.testng;

/*
 *  Copyright (c) 2017 Red Hat, Inc. and/or its affiliates.
 *  Copyright (c) 2017 INSA Lyon, CITI Laboratory.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CookieHandler;
import io.vertx.rxjava.ext.web.handler.SessionHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import io.vertx.rxjava.ext.web.sstore.LocalSessionStore;

/**
 * @author <a href="https://julien.ponge.org/">Julien Ponge</a>
 */
public class HttpServerVerticle extends AbstractVerticle {

    public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);


    @Override
    public void start(Future<Void> startFuture) throws Exception {


        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        // tag::static-assets[]
        router.get("/*").handler(StaticHandler.create().setCachingEnabled(false)); // <1> <2>
        router.get("/").handler(context -> context.reroute("/index.html"));
        // end::static-assets[]


        int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
        server
            .requestHandler(router::accept)
            .rxListen(portNumber)
            .subscribe(s -> {
                LOGGER.info("HTTP server running on port " + portNumber);
                startFuture.complete();
            }, t -> {
                LOGGER.error("Could not start a HTTP server", t);
                startFuture.fail(t);
            });
    }

}

