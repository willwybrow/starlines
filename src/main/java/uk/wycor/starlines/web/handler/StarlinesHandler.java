package uk.wycor.starlines.web.handler;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import uk.wycor.starlines.domain.StarlinesGame;
import uk.wycor.starlines.web.StarJson;
import uk.wycor.starlines.web.StarlineJson;
import uk.wycor.starlines.web.StarlineLegJson;
import uk.wycor.starlines.web.StarlineNetworkJson;

import java.util.stream.Collectors;

public class StarlinesHandler extends GameHandler {
    public StarlinesHandler(StarlinesGame starlinesGame) {
        super(starlinesGame);
    }

    @Override
    public void handle(RoutingContext event) {
        var starlineJson = StarlineNetworkJson.builder().starlines(starlinesGame
                        .getAllStarlines()
                        .stream()
                        .map(starline -> StarlineJson
                                .builder()
                                .id(starline.getId().toString())
                                .spans(starline
                                        .getNetwork()
                                        .stream()
                                        .map(starlineLeg -> StarlineLegJson
                                                .builder()
                                                .fromStar(StarJson.from(starlineLeg.getStarA()))
                                                .toStar(StarJson.from(starlineLeg.getStarB()))
                                                .sequesteredMass(starlineLeg.getSequesteredMass())
                                                .build()
                                        ).collect(Collectors.toList())
                                )
                                .build()
                        )
                        .toList())
                .build();
        event.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(starlineJson));
    }
}
