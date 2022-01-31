package uk.wycor.starlines.persistence;

import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.Probe;
import uk.wycor.starlines.domain.Star;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public record NewPlayerWork(
        Supplier<Player> newPlayerSupplier,
        Supplier<Collection<Probe>> startingProbeSupplier,
        Supplier<ClusterID> destinationClusterPicker,
        Function<ClusterID, Collection<Star>> getStarsInCluster,
        Function<Collection<Star>, Star> starPicker
) {
}
