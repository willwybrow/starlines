package uk.wycor.starlines.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.star.Star;
import uk.wycor.starlines.persistence.neo4j.StarRepository;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class StarController {

    private final StarRepository starRepository;


    @Autowired
    StarController(StarRepository starRepository) {
        this.starRepository = starRepository;

    }

    @GetMapping(
            path = "/api/stars/{starID}", produces = APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<Star>> getStar(@PathVariable("starID") UUID starID) {
        return starRepository.findById(starID)
                .map(star -> new ResponseEntity<>(star, new HttpHeaders(), HttpStatus.OK));
    }
}
