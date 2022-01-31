package uk.wycor.starlines.domain;

import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.ConfigurationSource;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.neo4j.Neo4jGameRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
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
        Player newPlayer = new Player(UUID.randomUUID(), playerName);
        Probe newProbe = new Probe(UUID.randomUUID());
        Supplier<ClusterID> freeClusterPicker = gameRepository::pickUnoccupiedCluster;
        Function<Collection<Star>, Star> bestStarPicker = this::bestStar;
        return gameRepository.setUpNewPlayer(
                () -> newPlayer,
                () -> List.of(newProbe),
                freeClusterPicker,
                gameRepository::getStarsInCluster,
                bestStarPicker
        );
    }

    public Star bestStar(Collection<Star> stars) {
        return Collections.max(stars, Comparator.comparingInt(Star::getMaximumMass));
    }

    public Map<HexPoint, StarControl> getClusterByID(ClusterID clusterID) {
        return this.gameRepository
                .getClusterControllers(clusterID)
                .stream()
                .collect(Collectors.toMap(starControl -> starControl.getStar().getCoordinate(), starControl -> starControl));
    }
}
