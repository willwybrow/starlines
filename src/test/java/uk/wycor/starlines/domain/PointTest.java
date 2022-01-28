package uk.wycor.starlines.domain;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {
    @Test
    void pointEqualsPointAtSameLocation() {
        assertEquals(new Point(0, 0), new Point(0, 0));
    }
}