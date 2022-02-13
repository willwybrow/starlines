package dev.wycobar.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.UniverseService;
import dev.wycobar.starlines.domain.star.Cluster;
import dev.wycobar.starlines.domain.star.ClusterID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ClusterController {
    private final UniverseService universeService;

    @Autowired
    ClusterController(UniverseService universeService) {
        this.universeService = universeService;
    }

    @GetMapping(
            path = "/api/cluster/{clusterNumber}", produces = APPLICATION_JSON_VALUE
    )
    public Mono<Cluster> getCluster(@PathVariable("clusterNumber") Long clusterNumber) {
        return universeService.getCluster(new ClusterID(clusterNumber));
    }
}
