package uk.wycor.starlines.domain;

import uk.wycor.starlines.persistence.neo4j.Neo4jGameRepository;

import java.util.UUID;
import java.util.stream.Stream;

public class UniverseManager {

    private final GameRepository gameRepository;

    public UniverseManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void expandUniverse() {
        gameRepository.populateNextStarfield(Starfield.generateRandomStarfield());
    }

    public static void main(String[] args) {
        UniverseManager universeManager = new UniverseManager(new Neo4jGameRepository());
        StarlinesGame starlinesGame = new StarlinesGame();

        Stream.of("Will", "Sam", "Ben", "Fran", "Carol", "Andy", "Wai", "Lucy").forEach(name -> {
            universeManager.expandUniverse();
            universeManager.gameRepository.setUpNewPlayer(new Player(UUID.randomUUID(), name));
        });

        starlinesGame.getClusterByID(new ClusterID(0));
    }

}
