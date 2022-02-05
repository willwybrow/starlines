package uk.wycor.starlines.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.domain.order.GivenOrder;
import uk.wycor.starlines.domain.order.OpenStarline;
import uk.wycor.starlines.domain.order.Order;
import uk.wycor.starlines.persistence.GameRepository;
import uk.wycor.starlines.persistence.neo4j.PlayerRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;
import uk.wycor.starlines.persistence.neo4j.StarlineRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class StarlinesGame {
    private final static Logger logger = Logger.getLogger(StarlinesGame.class.getName());

    private final GameRepository gameRepository = null;
    private final StarRepository starRepository;
    private final PlayerRepository playerRepository;
    private final StarlineRepository starlineRepository;

    @Autowired
    public StarlinesGame(StarRepository starRepository, PlayerRepository playerRepository, StarlineRepository starlineRepository) {
        this.starRepository = starRepository;
        this.playerRepository = playerRepository;
        this.starlineRepository = starlineRepository;
    }

    public Mono<Cluster> getCluster(ClusterID clusterID) {
        return starRepository.getInCluster(clusterID.getNumeric())
                .collect(Collectors.toList())
                .map(stars -> new Cluster(clusterID, stars));
    }

    public Instant nextTick() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfThisHour = now.truncatedTo(ChronoUnit.HOURS);
        return IntStream.range(0, 5)
                .map(i -> i * 15)
                .mapToObj(startOfThisHour::plusMinutes)
                .filter(time -> time.isAfter(now))
                .findFirst()
                .orElseThrow(RuntimeException::new) // TODO: something better
                .toInstant(ZoneOffset.UTC);
    }

    private Probe buildInitialProbe(Player owner) {
        logger.info("Generating new Probe for player " + owner.getName());
        return new Probe(UUID.randomUUID(), owner);
    }

    public Player setUpNewPlayer(String playerName) {
        /*
        1. create a player object for this player
        2. create the initial bunch of ships for this player
        3. find a vacant cluster and pick the best star
        4. save the player's starting ships to the above star
        */
        Player newPlayer = new Player(UUID.randomUUID(), playerName);
        return playerRepository.save(newPlayer).block();
        /*
        return gameRepository.setUpNewPlayer(new NewPlayerWork(
                () -> newPlayer,
                () -> Stream.generate(() -> buildInitialProbe(newPlayer)).limit(5).collect(Collectors.toSet()),
                gameRepository::pickUnoccupiedCluster,
                gameRepository::getStarsInCluster,
                this::bestStar
        ));

         */
    }

    public Collection<Starline> getAllStarlines() {
        return this.starlineRepository.findAll().collect(Collectors.toList()).block();
        // return this.gameRepository.getStarlinesInUniverse();
    }

    public Starline openStarline(Star fromStar, Star toStar, boolean mutual) {
        /*
        1. validate the starline can be opened between the two stars and calculate the mass that'll be "sequestered" in it
        2. are either of the two stars in starlines already? if they are both in different starlines the starlines will need to be merged
        3. save all changes
         */
        long distance = fromStar.getClusterID().distanceTo(toStar.getClusterID());
        long starlineMassCost = distance * 2;
        // each star must have the cost of a starline + 2 in mass before a starline can be opened
        // even if it's opened mutually
        if (fromStar.getCurrentMass() < starlineMassCost + 2) {
            // return failed
            return null;
        }
        if (toStar.getCurrentMass() < starlineMassCost + 2) {
            return null;
        }
        if (mutual) {
            fromStar.loseMass(starlineMassCost / 2);
            toStar.loseMass(starlineMassCost / 2);
        } else {
            fromStar.loseMass(starlineMassCost / 2);
        }

        StarlineLeg newStarlineLeg = new StarlineLeg(fromStar, toStar, starlineMassCost);

        Optional<Starline> fromStarInStarline = inStarline(fromStar);
        Optional<Starline> toStarInStarline = inStarline(toStar);

        if (fromStarInStarline.isPresent() && toStarInStarline.isPresent()) {
            return mergeStarline(fromStarInStarline.get(), toStarInStarline.get(), newStarlineLeg);
        } else if (fromStarInStarline.isPresent()) {
            return addStarToStarline(fromStarInStarline.get(), newStarlineLeg);
        } else if (toStarInStarline.isPresent()) {
            return addStarToStarline(toStarInStarline.get(), newStarlineLeg);
        } else {
            return createNewStarline(newStarlineLeg);
        }
    }

    private Starline createNewStarline(StarlineLeg newStarlineLeg) {
        return gameRepository.saveStarline(new Starline(UUID.randomUUID(), Set.of(newStarlineLeg)));
    }

    private Starline addStarToStarline(Starline starline, StarlineLeg newStarlineLeg) {
        starline.getNetwork().add(newStarlineLeg);
        return gameRepository.saveStarline(starline);
    }

    private Starline mergeStarline(Starline fromStarline, Starline toStarline, StarlineLeg newStarlineLeg) {
        gameRepository.deleteStarline(fromStarline);
        gameRepository.deleteStarline(toStarline);
        var newStarline = new Starline(UUID.randomUUID(), Stream.concat(Stream.of(fromStarline, toStarline).map(Starline::getNetwork).flatMap(Set::stream), Stream.of(newStarlineLeg)).collect(Collectors.toSet()));

        return gameRepository.saveStarline(newStarline);
    }

    public Optional<Starline> inStarline(Star star) {
        return getAllStarlines()
                .stream()
                .filter(starline -> starline.getNetwork().stream().flatMap(StarlineLeg::getBothStars).anyMatch(star::equals))
                .findFirst();
    }

    public Order giveOpenStarlineOrder(Probe assignee, Star target) {
        var givenOrder = GivenOrder
                .builder()
                .order(OpenStarline.builder().target(target).build())
                .performByTick(this.nextTick())
                .build();
        return givenOrder.getOrder();
    }

    public void processOrders(Instant tick) {
        /*
        1. get all orders in state unfulfilled with datetime = this tick
        2. for each order
         a.

         */
    }

    private Star bestStar(Collection<Star> stars) {
        return Collections.max(stars, Comparator.comparingLong(Star::getNaturalMassCapacity));
    }

    public Map<HexPoint, StarControl> getClusterByID(ClusterID clusterID) {
        return this.gameRepository.getStarsAndOrbitingProbesInCluster(clusterID)
                .stream()
                .collect(Collectors.toMap(starProbeOrbit -> starProbeOrbit.getStar().getCoordinate(), this::getStarControl));
    }

    public Map<ClusterID, Map<HexPoint, StarControl>> getClustersByID(Set<ClusterID> clusterIDs) {
        return this.gameRepository
                .getStarsAndOrbitingProbesInClusters(clusterIDs)
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue()
                                        .stream()
                                        .collect(Collectors.toMap(starProbeOrbit -> starProbeOrbit.getStar().getCoordinate(), this::getStarControl))));
    }

    private StarControl getStarControl(StarProbeOrbit starProbeOrbit) {
        return starProbeOrbit
                .getOrbitingProbes()
                .stream()
                .collect(Collectors.groupingBy(ship -> ship.ownedBy))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()))
                .values()
                .stream()
                .max(Integer::compare)
                .map(maxNumberOfShips -> new StarControl(
                        starProbeOrbit.getStar(),
                        starProbeOrbit
                                .getOrbitingProbes()
                                .stream()
                                .collect(Collectors.groupingBy(ship -> ship.ownedBy))
                                .entrySet()
                                .stream()
                                .filter(entry -> entry.getValue().size() == maxNumberOfShips)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toSet()),
                        maxNumberOfShips
                )).orElse(new StarControl(starProbeOrbit.getStar(), Collections.emptyList(), 0));

    }
}
