package uk.wycor.starlines.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import uk.wycor.starlines.RandomSample;
import uk.wycor.starlines.persistence.GameRepository;
import uk.wycor.starlines.persistence.neo4j.PlayerRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;
import uk.wycor.starlines.persistence.neo4j.StarlineRepository;
import uk.wycor.starlines.web.Application;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UniverseManager {

    private final static Logger logger = Logger.getLogger(UniverseManager.class.getName());

    private final GameRepository gameRepository = null;

    public UniverseManager(GameRepository gameRepository) {

    }

    public void expandUniverse() {
        gameRepository.populateNextStarfield(StarfieldGenerator::generateRandomStarfield);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext appContext = SpringApplication.run(Application.class, args);
        StarRepository starRepository = appContext.getBean(StarRepository.class);
        PlayerRepository playerRepository = appContext.getBean(PlayerRepository.class);
        StarlineRepository starlineRepository = appContext.getBean(StarlineRepository.class);

        UniverseManager universeManager = new UniverseManager(null);
        StarlinesGame starlinesGame = new StarlinesGame(starRepository, playerRepository, starlineRepository);
        /*
        PlayerNameGenerator.names().forEach(name -> {
            universeManager.expandUniverse();
            var player = starlinesGame.setUpNewPlayer(name);
            logger.info("Set up new player " + player.getName());
        });

        universeManager.expandUniverse();
        */

        var oneStar = RandomSample.pick(universeManager.gameRepository.getStarsAndOrbitingProbesInCluster(new ClusterID(0)));
        var anotherStar = RandomSample.pick(universeManager.gameRepository.getStarsAndOrbitingProbesInCluster(new ClusterID(14)));

        var starline = starlinesGame.openStarline(oneStar.getStar(), anotherStar.getStar(), true);
        logger.info(String.format("Opened starline ID %s between star %d:%s and %d:%s", starline.getId(), oneStar.getStar().getClusterID().getNumeric(), oneStar.getStar().getName(), anotherStar.getStar().getClusterID().getNumeric(), anotherStar.getStar().getName()));
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
