package uk.wycor.starlines.domain;

import java.util.UUID;

public class NewPlayerService {
    private final GameRepository gameRepository;

    public NewPlayerService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Player createNewPlayer(String playerName) {
        Player player = new Player(UUID.randomUUID(), playerName);
        return gameRepository.setUpNewPlayer(player);
    }
}
