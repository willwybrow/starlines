package dev.wycobar.starlines.domain.tick;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import dev.wycobar.starlines.domain.star.Star;
import dev.wycobar.starlines.persistence.neo4j.Neo4jTransactional;
import dev.wycobar.starlines.persistence.neo4j.StarRepository;

import java.time.Instant;
import java.util.logging.Logger;

@Component
@Order(10)
public class StarStability implements OnTickAction {
    private static final Logger logger = Logger.getLogger(StarStability.class.getName());

    private final StarRepository starRepository;

    public StarStability(StarRepository starRepository) {
        this.starRepository = starRepository;
    }

    @Override
    @Neo4jTransactional
    public Flux<Void> processActions(Instant thisTick, Instant nextTick) {
        logger.info("Recalculating Star instability");
        return starRepository
                .findAll() // we can optimise this in future by filtering only stars who either have some instability already or have orbiting harvesters
                .map(Star::recalculateInstability)
                .map(starRepository::save)
                .flatMap(Mono::then);
    }
}
