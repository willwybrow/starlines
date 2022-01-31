package uk.wycor.starlines.domain;

import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.NewPlayerWork;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface GameRepository {
    Player setUpNewPlayer(NewPlayerWork newPlayerWork);

    Set<StarControl> getClusterControllers(ClusterID clusterID);
    ClusterID populateNextStarfield(Map<HexPoint, Star> starfield);

    ClusterID pickUnoccupiedCluster();

    Collection<Star> getStarsInCluster(ClusterID clusterID);

}
