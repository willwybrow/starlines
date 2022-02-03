package uk.wycor.starlines.web.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class TerminatingHandler implements Handler<RoutingContext> {

    public abstract void handleEvent(RoutingContext event);

    @Override
    public void handle(RoutingContext event) {
        this.handleEvent(event);
        event.end();
    }
}
