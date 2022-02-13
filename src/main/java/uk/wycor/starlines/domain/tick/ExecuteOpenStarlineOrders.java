package uk.wycor.starlines.domain.tick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.StarlineSpan;
import uk.wycor.starlines.domain.order.MutualOpenStarline;
import uk.wycor.starlines.domain.ship.order.starline.OpenStarline;
import uk.wycor.starlines.domain.star.Star;
import uk.wycor.starlines.persistence.neo4j.Neo4jTransactional;
import uk.wycor.starlines.persistence.neo4j.OrderRepository;
import uk.wycor.starlines.persistence.neo4j.ProbeRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
@org.springframework.core.annotation.Order(5)
public class ExecuteOpenStarlineOrders extends ExecuteOrders<OpenStarline> {
    private static final Logger logger = Logger.getLogger(ExecuteOpenStarlineOrders.class.getName());

    private final ProbeRepository probeRepository;
    private final StarRepository starRepository;

    @Autowired
    public ExecuteOpenStarlineOrders(OrderRepository orderRepository,
                                     ProbeRepository probeRepository,
                                     StarRepository starRepository) {
        super(orderRepository);
        this.probeRepository = probeRepository;
        this.starRepository = starRepository;
    }

    @Override
    public Flux<OpenStarline> executeOrders(Instant thisTick, Instant nextTick) {
        return executeOpenStarlineOrders(thisTick);
    }

    @Neo4jTransactional
    private Flux<OpenStarline> executeOpenStarlineOrders(Instant forTick) {
        logger.info("Time to execute Open Starline orders, yay!");
        return probeRepository
                .findAll()
                .flatMap(probe -> Mono.justOrEmpty(probe
                                .getOrdersToOpenStarline()
                                .stream()
                                .filter(canExecuteOrder(forTick))
                                .findFirst()
                        )
                ).collect(Collectors.toSet())
                .flatMapMany(set -> {
                    final Set<MutualOpenStarline> mutualOrders = new HashSet<>();
                    final Set<OpenStarline> standaloneOrders = new HashSet<>();

                    set.forEach(openStarline -> {
                        Optional<OpenStarline> mutualOrder = set.stream().filter(order -> order.getOrderGivenTo().getOrbiting().equals(openStarline.getTarget())).findFirst();
                        if (mutualOrder.isPresent()) {
                            // then it is a mutual
                            logger.info("Found a matching order from the other star");
                            mutualOrders.add(new MutualOpenStarline(openStarline, mutualOrder.get())); // the Set representation of MutualOrder should ensure that this only goes in one way round
                        } else {
                            logger.info("Looks like a unilateral starline request");
                            standaloneOrders.add(openStarline);
                        }
                    });

                    return Flux
                            .fromIterable(mutualOrders)
                            .flatMap(mutualOrder -> openStarlineMutually(forTick, mutualOrder))
                            .map(MutualOpenStarline::stream)
                            .flatMap(Flux::fromStream)
                            .concatWith(Flux.fromIterable(standaloneOrders).flatMap(standaloneOrder -> openStarlineUnilaterally(forTick, standaloneOrder)));
                });
    }

    private Mono<MutualOpenStarline> openStarlineMutually(
            Instant executionTick,
            MutualOpenStarline mutualOpenStarline
    ) {
        Star fromStar = mutualOpenStarline.getA().getTarget();
        Star toStar = mutualOpenStarline.getB().getTarget();

        if (starlineSpanAlreadyExists(fromStar, toStar)) {
            return Mono.just(mutualOpenStarline);
        }

        var starlineLeg = validateRequirements(fromStar, toStar);

        fromStar.loseMass(starlineLeg.getSequesteredMass() / 2 + starlineLeg.getSequesteredMass() % 2); // remainder comes from here when odd
        toStar.loseMass(starlineLeg.getSequesteredMass() / 2);

        fromStar.getLinkedTo().add(starlineLeg);
        mutualOpenStarline.getA().setExecutedAt(executionTick);
        mutualOpenStarline.getB().setExecutedAt(executionTick);
        return orderRepository.saveAll(mutualOpenStarline.stream().toList())
                .then(starRepository.save(toStar))
                .then(starRepository.save(fromStar))
                .then(Mono.defer(() -> Mono.just(mutualOpenStarline)));
    }

    private Mono<OpenStarline> openStarlineUnilaterally(
            Instant executionTick,
            OpenStarline openStarline
    ) {
        Star fromStar = openStarline.getOrderGivenTo().getOrbiting();
        Star toStar = openStarline.getTarget();

        logger.info(String.format("Processing order to unilaterally open a starline from %s (%s) to %s (%s)", fromStar.getName(), fromStar.getId().toString(), toStar.getName(), toStar.getId().toString()));

        if (starlineSpanAlreadyExists(fromStar, toStar)) {
            logger.info("Seems this span of starline already exists");
            return Mono.just(openStarline);
        }

        var starlineLeg = validateRequirements(fromStar, toStar);

        fromStar.loseMass(starlineLeg.getSequesteredMass());

        fromStar.getLinkedTo().add(starlineLeg);
        openStarline.setExecutedAt(executionTick);
        logger.info(String.format("Star %s (%s) should now be persisted with a link to star %s (%s) (seqmass: %d)",
                fromStar.getName(),
                fromStar.getId().toString(),
                fromStar.getLinkedTo().stream().findFirst().orElseThrow().getStar().getName(),
                fromStar.getLinkedTo().stream().findFirst().orElseThrow().getStar().getId().toString(),
                fromStar.getLinkedTo().stream().findFirst().orElseThrow().getSequesteredMass()
                ));
        return orderRepository.save(openStarline)
                .then(starRepository.save(toStar))
                .then(starRepository.save(fromStar).doOnNext(star -> logger.info(String.format("Star %s saved with linkTo ID %d", star.getId(), star.getLinkedTo().stream().findFirst().map(StarlineSpan::getId).orElse(-1L)))))
                .then(Mono.defer(() -> Mono.just(openStarline)));
    }

    private boolean starlineSpanAlreadyExists(Star fromStar, Star toStar) {
        return fromStar.getLinkedTo().stream().map(StarlineSpan::getStar).anyMatch(toStar::equals)
                || toStar.getLinkedTo().stream().map(StarlineSpan::getStar).anyMatch(fromStar::equals); // starline leg already exists!
    }

    private StarlineSpan validateRequirements(Star fromStar, Star toStar) {
        long distance = fromStar.getCoordinates().distanceTo(toStar.getCoordinates());
        long starlineMassCost = distance * 2;
        // each star must have the cost of a starline + 2 in mass before a starline can be opened
        // even if it's opened mutually
        if (fromStar.getCurrentMass() < starlineMassCost + 2) {
            // return failed
            throw new RuntimeException("Source star does not fulfil minimum mass requirement");
        }
        if (toStar.getCurrentMass() < starlineMassCost + 2) {
            throw new RuntimeException("Destination star does not fulfil minimum mass requirement");
        }

        logger.info(String.format("Starline goes %d AU away and costs %d", distance, starlineMassCost));

        return StarlineSpan
                .builder()
                .starlineID(UUID.randomUUID())
                .star(toStar)
                .sequesteredMass(starlineMassCost).build();
    }
}
