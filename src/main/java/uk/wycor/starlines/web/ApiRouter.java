package uk.wycor.starlines.web;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import uk.wycor.starlines.domain.StarlinesGame;
import uk.wycor.starlines.web.handler.ClusterHandler;
import uk.wycor.starlines.web.handler.StarlinesHandler;


public class ApiRouter {
    private final Router router;

    public ApiRouter(Vertx vertx, StarlinesGame starlinesGame) {
        this.router = Router.router(vertx);
        this.router.route().handler(req -> { req.response().putHeader("next-tick", starlinesGame.nextTick().toString()); req.next(); });
        this.router
                .get("/api/cluster/:clusterID")
                .produces("application/json")
                .handler(new ClusterHandler(starlinesGame))
                .enable();
        this.router
            .get("/api/starlines/")
                .produces("application/json")
                .handler(new StarlinesHandler(starlinesGame))
                .enable();

        // default 404
        this.router.route().handler(req -> req.response().setStatusCode(404).putHeader("content-type", "text/plain; charset=\"utf-8\"").end("Not found"));
    }

    public Router getRouter() {
        return router;
    }
}
