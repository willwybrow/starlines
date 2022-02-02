package uk.wycor.starlines.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public enum Order {
    OPEN_STARLINE(Set.of(Probe.class), Set.of(Star.class)),
    CLOSE_STARLINE(Set.of(Probe.class, Harvester.class), Collections.emptyList()),
    STABILISE_STAR(Set.of(Stabiliser.class), Collections.emptyList()),
    INCREASE_MASS(Set.of(Harvester.class), Collections.emptyList());

    private final Collection<Class<? extends Ship>> capableShips;

    Collection<Class<? extends Ship>> capableShips() {
        return this.capableShips;
    }

    Order(Collection<Class<? extends Ship>> capableShips, Collection<Class<? extends GameObject>> validTargets) {
        this.capableShips = capableShips;
    }
}
