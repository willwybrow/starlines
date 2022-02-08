package uk.wycor.starlines.domain.star;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClusterIDTest {

    @ParameterizedTest
    @MethodSource("naturalToInteger")
    void naturalToInteger(long natural, long integer) {
        assertEquals(integer, ClusterID.naturalToInteger(natural));
    }

    @ParameterizedTest
    @MethodSource("naturalToInteger")
    void integerToNatural(long natural, long integer) {
        assertEquals(natural, ClusterID.integerToNatural(integer));
    }

    @ParameterizedTest
    @MethodSource("clusterCoordinateToClusterID")
    void coordinateToClusterID(long q, long r, long clusterID) {
        assertEquals(clusterID, ClusterID.clusterID(new HexPoint(q, r)));
    }

    @ParameterizedTest
    @MethodSource("clusterCoordinateToClusterID")
    void clusterIDToCoordinate(long q, long r, long clusterID) {
        HexPoint hexPoint = ClusterID.coordinate(clusterID);
        assertEquals(q, hexPoint.q());
        assertEquals(r, hexPoint.r());
    }

    private static Stream<Arguments> naturalToInteger() {
        return Stream.of(
                Arguments.of(-5, 9),
                Arguments.of(-4, 7),
                Arguments.of(-3, 5),
                Arguments.of(-2, 3),
                Arguments.of(-1, 1),
                Arguments.of(0, 0),
                Arguments.of(1, 2),
                Arguments.of(2, 4),
                Arguments.of(3, 6),
                Arguments.of(4, 8),
                Arguments.of(5, 10)
        );
    }

    private static Stream<Arguments> clusterCoordinateToClusterID() {
        return Stream.of(
                Arguments.of(0, 0, 0),
                Arguments.of(1, 2, 18),
                Arguments.of(-1, 2, 17),
                Arguments.of(1, -2, 11),
                Arguments.of(-1, -2, 10)
        );
    }
}