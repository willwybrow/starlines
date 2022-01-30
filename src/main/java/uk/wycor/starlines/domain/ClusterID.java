package uk.wycor.starlines.domain;

import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.domain.geometry.Pair;
import uk.wycor.starlines.domain.geometry.Szudzik;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClusterID {
    private static final List<Pair> AXIAL_DIRECTION_VECTORS = Arrays.asList(
            new Pair(1, -1),
            new Pair(1, 0),
            new Pair(0 ,1),
            new Pair(-1, 1),
            new Pair(-1, 0),
            new Pair(0, -1)
    );

    public static long naturalToInteger(long natural) {
        if (natural < 0) {
            return (-2 * natural) - 1;
        }
        return 2 * natural;
    }

    public static long integerToNatural(long integer) {
        if (integer < 0) {
            throw new RuntimeException("Cannot be negative");
        }
        return integer % 2 == 0 ? integer / 2 : (integer + 1) / -2;
    }

    public static long clusterID(HexPoint hexPoint) {
        return Szudzik.pair(naturalToInteger(hexPoint.q()), naturalToInteger(hexPoint.r()));
    }

    public static HexPoint coordinate(long clusterID) {
        var pair = Szudzik.unpair(clusterID);
        return new HexPoint(integerToNatural(pair.a()), integerToNatural(pair.b()));
    }

    public static List<Long> neighbourClusterIDs(long clusterID) {
        HexPoint origin = coordinate(clusterID);
        return AXIAL_DIRECTION_VECTORS
                .stream()
                .map(directionPair -> origin.translate(directionPair.a(), directionPair.b()))
                .map(ClusterID::clusterID)
                .collect(Collectors.toList());

    }
}
