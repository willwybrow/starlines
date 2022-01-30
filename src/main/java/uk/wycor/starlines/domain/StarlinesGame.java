package uk.wycor.starlines.domain;

import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.ConfigurationSource;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.neo4j.Neo4jGameRepository;

import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<HexPoint, Star> getClusterByID(int clusterID) {
        return this.gameRepository
                .getClusterControllers(clusterID)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(starListEntry -> starListEntry.getKey().getCoordinate(), Map.Entry::getKey));
    }
}
