package uk.wycor.starlines.web;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WebServer {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<AsyncResult<String>> q = new ArrayBlockingQueue<>(1);
        Vertx.vertx().deployVerticle(new MainVerticle(), q::offer);
        AsyncResult<String> result = q.take();
        if (result.failed()) {
            throw new RuntimeException(result.cause());
        }
    }
}
