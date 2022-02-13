package uk.wycor.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.star.Star;
import uk.wycor.starlines.persistence.neo4j.StarRepository;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class StarlinesController {

    private final static Logger logger = Logger.getLogger(StarlinesController.class.getName());

    private final StarRepository starRepository;

    @Autowired
    StarlinesController(StarRepository starRepository) {
        this.starRepository = starRepository;

    }

    @GetMapping(
            path = "/api/starlines/{starlineID}", produces = APPLICATION_JSON_VALUE
    )
    public Mono<Set<Star>> getStarline(@PathVariable("starlineID") UUID starlineID) {
        return starRepository.findByLinkedToStarlineID(starlineID)
                .collect(Collectors.toSet());
    }
}
