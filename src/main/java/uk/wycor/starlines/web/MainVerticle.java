package uk.wycor.starlines.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import uk.wycor.starlines.domain.StarlinesGame;

public class MainVerticle extends AbstractVerticle {
    private final StarlinesGame starlinesGame;

    public MainVerticle() {
        this.starlinesGame = new StarlinesGame();
    }

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.createHttpServer()
                .requestHandler(new ApiRouter(vertx, starlinesGame).getRouter())
                .listen(8888, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("HTTP server started on port 8888");
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }
}
