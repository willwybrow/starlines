package uk.wycor.starlines.persistence;

import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.StarProbeOrbit;
import uk.wycor.starlines.domain.Starline;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface GameRepository {
    Player setUpNewPlayer(NewPlayerWork newPlayerWork);

    Set<StarProbeOrbit> getStarsAndOrbitingProbesInCluster(ClusterID clusterID);

    Map<ClusterID, Set<StarProbeOrbit>> getStarsAndOrbitingProbesInClusters(Set<ClusterID> clusterIDs);

    ClusterID populateNextStarfield(Function<ClusterID, Map<HexPoint, Star>> starfieldGenerator);

    ClusterID pickUnoccupiedCluster();

    Collection<Star> getStarsInCluster(ClusterID clusterID);

    Collection<Starline> getStarlinesInUniverse();

    void deleteStarline(Starline starline);

    Starline saveStarline(Starline newStarline);
}
