package uk.wycor.starlines.domain.geometry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HexPointTest {

    @ParameterizedTest
    @MethodSource("sForValidQAndR")
    void sCalculatesCorrectly(int s, int q, int r) {
        assertEquals(s, new HexPoint(q, r).s());
    }

    @Test
    void pointEqualsPointAtSameLocation() {
        assertEquals(new HexPoint(0, 0), new HexPoint(0, 0));
    }

    private static Stream<Arguments> sForValidQAndR() {
        // these are all the coordinates on https://www.redblobgames.com/grids/hexagons/
        return Stream.of(
                Arguments.of(3, 0, -3),
                Arguments.of(3, -1, -2),
                Arguments.of(3, -2, -1),
                Arguments.of(3, -3, 0),
                Arguments.of(2, 1, -3),
                Arguments.of(2, 0, -2),
                Arguments.of(2, -1, -1),
                Arguments.of(2, -2, 0),
                Arguments.of(2, -3, 1),
                Arguments.of(1, 2, -3),
                Arguments.of(1, 1, -2),
                Arguments.of(1, 0, -1),
                Arguments.of(1, -1, 0),
                Arguments.of(1, -2, 1),
                Arguments.of(1, -3, 2),
                Arguments.of(0, 3, -3),
                Arguments.of(0, 2, -2),
                Arguments.of(0, 1, -1),
                Arguments.of(0, 0, 0),
                Arguments.of(0, -1, 1),
                Arguments.of(0, -2, 2),
                Arguments.of(0, -3, 3),
                Arguments.of(-1, 3, -2),
                Arguments.of(-1, 2, -1),
                Arguments.of(-1, 1, 0),
                Arguments.of(-1, 0, 1),
                Arguments.of(-1, -1, 2),
                Arguments.of(-1, -2, 3),
                Arguments.of(-2, 3, -1),
                Arguments.of(-2, 2, 0),
                Arguments.of(-2, 1, 1),
                Arguments.of(-2, 0, 2),
                Arguments.of(-2, -1, 3),
                Arguments.of(-3, 3, 0),
                Arguments.of(-3, 2, 1),
                Arguments.of(-3, 1, 2),
                Arguments.of(-3, 0, 3)
        );
    }
}