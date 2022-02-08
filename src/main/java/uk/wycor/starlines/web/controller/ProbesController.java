package uk.wycor.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.ship.Probe;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.web.filter.AuthenticationFilter;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ProbesController {
    private final ProbeRepository probeRepository;

    @Autowired
    public ProbesController(ProbeRepository probeRepository) {
        this.probeRepository = probeRepository;
    }

    @GetMapping(
            path = "/api/ships/my/probes/", produces = APPLICATION_JSON_VALUE
    )
    public Mono<Set<Probe>> getMyProbes() {
        return Mono
                .deferContextual(AuthenticationFilter::getFromContext)
                .map(Player::getId)
                .flatMapMany(probeRepository::getMyProbes)
                .collect(Collectors.toSet());
    }
}
