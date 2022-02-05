package uk.wycor.starlines.domain;

import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.ConfigurationSource;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.domain.order.GivenOrder;
import uk.wycor.starlines.domain.order.OpenStarline;
import uk.wycor.starlines.domain.order.Order;
import uk.wycor.starlines.persistence.NewPlayerWork;
import uk.wycor.starlines.persistence.neo4j.Neo4jGameRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StarlinesGame {
    private final static Logger logger = Logger.getLogger(StarlinesGame.class.getName());

    private final static ConfigurationSource CONFIGURATION_SOURCE = new ClasspathConfigurationSource("game.properties");
    private final GameRepository gameRepository;

    public StarlinesGame() {
        if (CONFIGURATION_SOURCE.properties().getProperty("repository-class").equals(Neo4jGameRepository.class.getName())) {
            this.gameRepository = new Neo4jGameRepository();
        } else {
            throw new RuntimeException("Unconfigured repository");
        }
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
        return gameRepository.setUpNewPlayer(new NewPlayerWork(
                () -> newPlayer,
                () -> Stream.generate(() -> buildInitialProbe(newPlayer)).limit(5).collect(Collectors.toSet()),
                gameRepository::pickUnoccupiedCluster,
                gameRepository::getStarsInCluster,
                this::bestStar
        ));
    }

    public Collection<Starline> getAllStarlines() {
        return this.gameRepository.getStarlinesInUniverse();
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
        return Collections.max(stars, Comparator.comparingInt(Star::getNaturalMassCapacity));
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
