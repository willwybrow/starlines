package uk.wycor.starlines.domain;

import uk.wycor.starlines.persistence.neo4j.Neo4jGameRepository;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UniverseManager {

    private final static Logger logger = Logger.getLogger(UniverseManager.class.getName());

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

        PlayerNameGenerator.names().forEach(name -> {
            universeManager.expandUniverse();
            var player = starlinesGame.setUpNewPlayer(name);
            logger.info("Set up new player " + player.getName());
        });

        universeManager.expandUniverse();

        starlinesGame.getClusterByID(new ClusterID(0));
    }

    static class PlayerNameGenerator {
        private static final String[] INITIALS = {
                "B", "S", "W", "C", "Fr", "L"
        };
        private static final String[] FINALS = {
                "en", "am", "ill", "arol", "an", "ucy", "andy"
        };

        public static List<String> names() {
            return Arrays.stream(INITIALS).flatMap(i -> Arrays.stream(FINALS).map(f -> i + f)).collect(Collectors.toList());
        }
    }

}
