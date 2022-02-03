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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StarlinesGame {
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

    public Player setUpNewPlayer(String playerName) {
        /*
        1. create a player object for this player
        2. create the initial bunch of ships for this player
        3. find a vacant cluster and pick the best star
        4. save the player's starting ships to the above star
        */
        return gameRepository.setUpNewPlayer(new NewPlayerWork(
                () -> new Player(UUID.randomUUID(), playerName),
                () -> IntStream.range(0, 5).mapToObj(i -> new Probe(UUID.randomUUID())).collect(Collectors.toSet()),
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
        return this.gameRepository
                .getClusterControllers(clusterID)
                .stream()
                .collect(Collectors.toMap(starControl -> starControl.getStar().getCoordinate(), starControl -> starControl));
    }

    public Map<ClusterID, Map<HexPoint, StarControl>> getClustersByID(Collection<ClusterID> clusterIDs) {
        return this.gameRepository
                .getClustersAndControllers(clusterIDs)
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue()
                                        .stream()
                                        .collect(Collectors.toMap(starControl -> starControl.getStar().getCoordinate(), starControl -> starControl))
                        )
                );
    }
}
