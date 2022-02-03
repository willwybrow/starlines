package uk.wycor.starlines.domain;

import uk.wycor.starlines.persistence.neo4j.Neo4jGameRepository;

import java.util.stream.Stream;

public class UniverseManager {

    private final GameRepository gameRepository;

    public UniverseManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void expandUniverse() {
        gameRepository.populateNextStarfield(StarfieldGenerator::generateRandomStarfield);
    }

    public static void main(String[] args) {
        UniverseManager universeManager = new UniverseManager(new Neo4jGameRepository());
        StarlinesGame starlinesGame = new StarlinesGame();

        Stream.of("Guy", "Gal", "Buy", "Bal", "Huy", "Hal").forEach(name -> {
            universeManager.expandUniverse();
            starlinesGame.setUpNewPlayer(name);
        });

        starlinesGame.getClusterByID(new ClusterID(0));
    }

}
