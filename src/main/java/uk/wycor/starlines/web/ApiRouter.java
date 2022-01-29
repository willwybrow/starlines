package uk.wycor.starlines.web;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import uk.wycor.starlines.domain.StarlinesGame;


public class ApiRouter {
    private final Router router;

    public ApiRouter(Vertx vertx, StarlinesGame starlinesGame) {
        this.router = Router.router(vertx);
        this.router
                .get("/api/cluster/:clusterID")
                .produces("application/json")
                .handler(new ClusterHandler(starlinesGame))
                .enable();
    }

    public Router getRouter() {
        return router;
    }
}
