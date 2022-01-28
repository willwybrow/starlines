package uk.wycor.starlines.domain;

public class UniverseManager {
    public static final int MASS_PER_NEW_CLUSTER = 20;
    public static final int CLUSTER_SIZE = 5;

    private final GameRepository gameRepository;

    public UniverseManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void expandUniverse() {

    }

}
