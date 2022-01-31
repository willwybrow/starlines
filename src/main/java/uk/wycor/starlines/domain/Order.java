package uk.wycor.starlines.domain;

import java.util.Collection;
import java.util.Set;

public enum Order {
    OPEN_STARLINE(Probe.class),
    CLOSE_STARLINE(Probe.class, Harvester.class),
    STABILISE_STAR(Stabiliser.class),
    INCREASE_MASS(Harvester.class);

    private final Class<? extends Ship>[] capableShips;

    Collection<Class<? extends Ship>> capableShips() {
        return Set.of(this.capableShips);
    }

    @SafeVarargs
    Order(Class<? extends Ship>... capableShips) {

        this.capableShips = capableShips;
    }
}
