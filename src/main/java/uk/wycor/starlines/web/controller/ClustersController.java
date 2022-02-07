package uk.wycor.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.Cluster;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.StarlinesGame;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ClustersController {
    private final StarlinesGame starlinesGame;


    @Autowired
    public ClustersController(StarlinesGame starlinesGame) {
        this.starlinesGame = starlinesGame;
    }

    @GetMapping(
            path = "/api/clusters/", produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<Map<Long, Cluster>>> getCluster(@RequestParam(name = "q") List<Long> qValues, @RequestParam(name="r") List<Long> rValues) {
        var qStatistics = qValues.stream().mapToLong(Long::longValue).summaryStatistics();
        var rStatistics = rValues.stream().mapToLong(Long::longValue).summaryStatistics();
        var clusterIDs = LongStream
                .range(qStatistics.getMin(), qStatistics.getMax() + 1)
                .boxed()
                .flatMap(q -> LongStream
                        .range(rStatistics.getMin(), rStatistics.getMax() + 1)
                        .mapToObj(r -> new HexPoint(q, r))
                ).map(ClusterID::new)
                .collect(Collectors.toSet());
        return starlinesGame
                .getClusters(clusterIDs)
                .collectMap(cluster -> cluster.getClusterID().getNumeric(), cluster -> cluster)
                .map(cluster -> new ResponseEntity<>(cluster, new HttpHeaders(), HttpStatus.OK));
    }
}
