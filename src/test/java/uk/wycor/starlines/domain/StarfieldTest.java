package uk.wycor.starlines.domain;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StarfieldTest {
    @Test
    void allPossibleCoordinates() {
        Set<Point> allPossibleCoordinates = new HashSet<>(Starfield.ALL_POSSIBLE_CLUSTER_COORDINATES);

        assertTrue(allPossibleCoordinates.contains(new Point(0, 0)));
        assertTrue(allPossibleCoordinates.contains(new Point(4, 4)));
        assertEquals(25, allPossibleCoordinates.size());
    }

}