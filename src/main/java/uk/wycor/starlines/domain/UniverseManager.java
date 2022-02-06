package uk.wycor.starlines.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import uk.wycor.starlines.web.Application;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UniverseManager {

    private final static Logger logger = Logger.getLogger(UniverseManager.class.getName());

    public static void main(String[] args) {
        ConfigurableApplicationContext appContext = SpringApplication.run(Application.class, args);
        UniverseService universeService = appContext.getBean(UniverseService.class);

        /*
        PlayerNameGenerator.names().forEach(name -> {
            universeManager.expandUniverse();
            var player = starlinesGame.setUpNewPlayer(name);
            logger.info("Set up new player " + player.getName());
        });

        universeManager.expandUniverse();
        */

        // var oneStar = RandomSample.pick(universeManager.gameRepository.getStarsAndOrbitingProbesInCluster(new ClusterID(0)));
        // var anotherStar = RandomSample.pick(universeManager.gameRepository.getStarsAndOrbitingProbesInCluster(new ClusterID(14)));

        // var starline = starlinesGame.openStarline(oneStar.getStar(), anotherStar.getStar(), true);
        // logger.info(String.format("Opened starline ID %s between star %d:%s and %d:%s", starline.getId(), oneStar.getStar().getClusterID().getNumeric(), oneStar.getStar().getName(), anotherStar.getStar().getClusterID().getNumeric(), anotherStar.getStar().getName()));

        logger.info("Started this whole mess...");

        var cluster = universeService.expandUniverse().block(Duration.ofSeconds(2));
//                .subscribe(newCluster ->
//                {
//                    logger.info(String.format("Created new cluster %d", newCluster.getClusterID().getNumeric()));
//                    SpringApplication.exit(appContext);
//                }
//        ).dispose();

        // appContext.stop();
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
