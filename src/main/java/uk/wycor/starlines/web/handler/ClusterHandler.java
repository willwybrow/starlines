package uk.wycor.starlines.web.handler;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.StarlinesGame;
import uk.wycor.starlines.web.ClusterJson;

public class ClusterHandler extends GameHandler {

    public ClusterHandler(StarlinesGame starlinesGame) {
        super(starlinesGame);
    }

    @Override
    public void handle(RoutingContext event) {
        var clusterID = new ClusterID(Long.parseLong(event.pathParam("clusterID")));
        var starControlMap = starlinesGame.getClusterByID(clusterID);
        event.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(
                        ClusterJson.from(clusterID, starControlMap)
                ));
    }
}
