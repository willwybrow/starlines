package uk.wycor.starlines.domain;

import java.util.Map;

public interface GameRepository {
    Player setUpNewPlayer(Player player);
    int populateNextStarfield(Map<Point, Star> starfield);
}
