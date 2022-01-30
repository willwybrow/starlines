package uk.wycor.starlines.domain;

import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.List;
import java.util.Map;

public interface GameRepository {
    Player setUpNewPlayer(Player player);

    Map<Star, List<Player>> getClusterControllers(int clusterID);

    int populateNextStarfield(Map<HexPoint, Star> starfield);
}
