package uk.wycor.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.Cluster;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.StarlinesGame;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ClusterController {
    private final StarlinesGame starlinesGame;

    @Autowired
    ClusterController(StarlinesGame starlinesGame) {
        this.starlinesGame = starlinesGame;
    }

    @GetMapping(
            path = "/api/cluster/{clusterNumber}", produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<Cluster>> getCluster(@PathVariable("clusterNumber") Long clusterNumber) {
        return starlinesGame
                .getCluster(new ClusterID(clusterNumber))
                        .map(cluster -> new ResponseEntity<>(cluster, new HttpHeaders(), HttpStatus.OK));
    }
}
