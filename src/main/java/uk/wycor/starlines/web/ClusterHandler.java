package uk.wycor.starlines.web;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import uk.wycor.starlines.domain.StarlinesGame;

import java.util.stream.Collectors;

public class ClusterHandler implements Handler<RoutingContext> {

    private final StarlinesGame starlinesGame;

    public ClusterHandler(StarlinesGame starlinesGame) {
        this.starlinesGame = starlinesGame;
    }

    @Override
    public void handle(RoutingContext event) {
        int clusterID = Integer.parseInt(event.pathParam("clusterID"));
        var starMap = starlinesGame.getClusterByID(clusterID);
        event.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(
                        new ClusterJson(
                                starMap
                                        .entrySet()
                                        .stream()
                                        .map(pointStarEntry -> StarJson.builder()
                                                .id(pointStarEntry.getValue().getId().toString())
                                                .currentMass(pointStarEntry.getValue().getCurrentMass())
                                                .maximumMass(pointStarEntry.getValue().getMaximumMass()
                                                ).coordinates(PointJson.fromPoint(pointStarEntry.getKey()))
                                                .build()).collect(Collectors.toList()))
                ));
    }
}
