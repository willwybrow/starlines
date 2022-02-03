package uk.wycor.starlines.web.handler;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.StarlinesGame;
import uk.wycor.starlines.web.ClusterJson;
import uk.wycor.starlines.web.ClusterMetadataJson;
import uk.wycor.starlines.web.StarJson;

import java.util.stream.Collectors;

public class ClusterHandler extends GameHandler {

    public ClusterHandler(StarlinesGame starlinesGame) {
        super(starlinesGame);
    }

    @Override
    public void handle(RoutingContext event) {
        var clusterID = new ClusterID(Integer.parseInt(event.pathParam("clusterID")));
        var starMap = starlinesGame.getClusterByID(clusterID);
        event.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(
                        new ClusterJson(
                                new ClusterMetadataJson(clusterID),
                                starMap
                                        .values()
                                        .stream()
                                        .map(StarJson::from)
                                        .collect(Collectors.toList()))
                ));
    }
}
