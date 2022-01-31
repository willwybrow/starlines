package uk.wycor.starlines.domain;

import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public interface GameRepository {
    Player setUpNewPlayer(
            Supplier<Player> newPlayerSupplier,
            Supplier<Collection<Probe>> startingProbeSupplier,
            Supplier<ClusterID> destinationClusterPicker,
            Function<ClusterID, Collection<Star>> getStarsInCluster,
            Function<Collection<Star>, Star> starPicker
    );

    Set<StarControl> getClusterControllers(ClusterID clusterID);
    ClusterID populateNextStarfield(Map<HexPoint, Star> starfield);

    ClusterID pickUnoccupiedCluster();

    Collection<Star> getStarsInCluster(ClusterID clusterID);

    Collection<Star> bestStarsInCluster(ClusterID clusterID);
}
