package uk.wycor.starlines.domain;

import org.junit.jupiter.api.Test;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.wycor.starlines.domain.UniverseManager.CLUSTER_SUBDIVISIONS;

class StarfieldTest {
    @Test
    void clusterSubdivisionIsOdd() {
        assertEquals(1, CLUSTER_SUBDIVISIONS % 2);
    }

    @Test
    void allPossibleCoordinates() {
        Set<HexPoint> allPossibleCoordinates = new HashSet<>(Starfield.ALL_POSSIBLE_CLUSTER_COORDINATES);

        assertTrue(allPossibleCoordinates.contains(new HexPoint(0, 0)));
        assertTrue(allPossibleCoordinates.contains(new HexPoint(0, -3)));
        assertTrue(allPossibleCoordinates.contains(new HexPoint(0, 3)));
        assertTrue(allPossibleCoordinates.contains(new HexPoint(-3, 3)));
        assertTrue(allPossibleCoordinates.contains(new HexPoint(3, -3)));
        assertEquals(37, allPossibleCoordinates.size());
    }

}