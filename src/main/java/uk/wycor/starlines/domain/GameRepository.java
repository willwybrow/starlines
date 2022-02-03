package uk.wycor.starlines.domain;

import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.NewPlayerWork;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface GameRepository {
    Player setUpNewPlayer(NewPlayerWork newPlayerWork);

    Set<StarControl> getClusterControllers(ClusterID clusterID);
    ClusterID populateNextStarfield(Function<ClusterID, Map<HexPoint, Star>> starfieldGenerator);

    ClusterID pickUnoccupiedCluster();

    Collection<Star> getStarsInCluster(ClusterID clusterID);

    Collection<Starline> getStarlinesInUniverse();
}
