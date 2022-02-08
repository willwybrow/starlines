package uk.wycor.starlines.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import uk.wycor.starlines.persistence.neo4j.PlayerRepository;
import uk.wycor.starlines.persistence.neo4j.StarRepository;
import uk.wycor.starlines.web.Application;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UniverseManager {

    private final static Logger logger = Logger.getLogger(UniverseManager.class.getName());

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext appContext = SpringApplication.run(Application.class, args);
        UniverseService universeService = appContext.getBean(UniverseService.class);
        PlayerRepository playerRepository = appContext.getBean(PlayerRepository.class);
        StarRepository starRepository = appContext.getBean(StarRepository.class);

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

        playerRepository.save(Player.builder().name("Brian").build()).subscribe(
                data -> logger.info(String.format("found post: %s", data)),
                err -> logger.warning(String.format("error: %s", err)),
                () -> logger.info("done"));



        var cluster = universeService.expandUniverse()
                .subscribe(
                        data -> logger.info(String.format("found post: %s", data)),
                        err -> logger.warning(String.format("error: %s", err)),
                        () -> logger.info("done")
                );
//                .subscribe(newCluster ->
//                {
//                    logger.info(String.format("Created new cluster %d", newCluster.getClusterID().getNumeric()));
//                    appContext.stop();
//                    System.exit(SpringApplication.exit(appContext));
//                }
//        );
    }

    public static class PlayerNameGenerator {
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
