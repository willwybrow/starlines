package dev.wycobar.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.UniverseService;
import dev.wycobar.starlines.domain.geometry.HexPoint;
import dev.wycobar.starlines.domain.star.Cluster;
import dev.wycobar.starlines.domain.star.ClusterID;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ClustersController {
    private final UniverseService universeService;

    @Autowired
    public ClustersController(UniverseService universeService) {
        this.universeService = universeService;
    }

    @GetMapping(
            path = "/api/clusters/", produces = APPLICATION_JSON_VALUE
    )
    public Mono<Map<Long, Cluster>> getClusters(@RequestParam(name = "q") List<Long> qValues, @RequestParam(name="r") List<Long> rValues) {
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
        return universeService.getClusters(clusterIDs)
                .collectMap(cluster -> cluster.getClusterID().getNumeric(), cluster -> cluster);
    }
}
