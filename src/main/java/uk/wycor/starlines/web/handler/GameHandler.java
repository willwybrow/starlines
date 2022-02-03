package uk.wycor.starlines.web.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import uk.wycor.starlines.domain.StarlinesGame;

public abstract class GameHandler implements Handler<RoutingContext> {
    protected final StarlinesGame starlinesGame;

    public GameHandler(StarlinesGame starlinesGame) {
        this.starlinesGame = starlinesGame;
    }
}
