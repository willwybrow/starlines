package uk.wycor.starlines.web.handler;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.StarlinesGame;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.web.ClusterJson;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class ClustersHandler extends ClusterHandler {
    public ClustersHandler(StarlinesGame starlinesGame) {
        super(starlinesGame);
    }

    @Override
    public void handle(RoutingContext event) {
        var qMinMax = Arrays
                .stream(event.queryParam("q").get(0).split(","))
                .map(Long::parseLong)
                .collect(Collectors.summarizingLong(Long::longValue));
        var rMinMax = Arrays
                .stream(event.queryParam("r").get(0).split(","))
                .map(Long::parseLong)
                .collect(Collectors.summarizingLong(Long::longValue));
        var clustersToLoad = LongStream.range(qMinMax.getMin(), qMinMax.getMax() + 1)
                .mapToObj(q -> LongStream.range(rMinMax.getMin(), rMinMax.getMax() + 1)
                        .mapToObj(r -> new HexPoint(q, r))
                        .map(ClusterID::new))
                .flatMap(Stream::distinct)
                .collect(Collectors.toSet());
        var multiClusterStarMap = starlinesGame.getClustersByID(clustersToLoad);
        event.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(
                        multiClusterStarMap
                                .entrySet()
                                .stream()
                                .map(entry -> ClusterJson.from(entry.getKey(), entry.getValue()))
                                .collect(Collectors.toList())
                ));
    }
}
