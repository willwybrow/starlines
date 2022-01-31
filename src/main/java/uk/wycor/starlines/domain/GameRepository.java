package uk.wycor.starlines.domain;

import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.Map;
import java.util.Set;

public interface GameRepository {
    Player setUpNewPlayer(Player player);

    Set<StarControl> getClusterControllers(ClusterID clusterID);
    ClusterID populateNextStarfield(Map<HexPoint, Star> starfield);
}
