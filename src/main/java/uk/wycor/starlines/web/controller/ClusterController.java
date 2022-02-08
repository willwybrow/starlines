package uk.wycor.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.UniverseService;
import uk.wycor.starlines.domain.star.Cluster;
import uk.wycor.starlines.domain.star.ClusterID;

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
    public Mono<ResponseEntity<Cluster>> getCluster(@PathVariable("clusterNumber") Long clusterNumber) {
        return universeService.getCluster(new ClusterID(clusterNumber))
                        .map(cluster -> new ResponseEntity<>(cluster, new HttpHeaders(), HttpStatus.OK));
    }
}
