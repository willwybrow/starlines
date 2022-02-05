package uk.wycor.starlines.domain;

import lombok.Getter;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.domain.geometry.Pair;
import uk.wycor.starlines.domain.geometry.Szudzik;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class ClusterID {
    private final long numeric;
    private final HexPoint coordinates;

    public ClusterID(long numeric) {
        this.numeric = numeric;
        this.coordinates = ClusterID.coordinate(numeric);
    }

    public ClusterID(HexPoint coordinates) {
        this.coordinates = coordinates;
        this.numeric = ClusterID.clusterID(coordinates);
    }

    public Long distanceTo(ClusterID otherCluster) {
        return this.coordinates.distanceTo(otherCluster.coordinates);
    }

    static final List<Pair> AXIAL_DIRECTION_VECTORS = Arrays.asList(
            new Pair(1, -1),
            new Pair(1, 0),
            new Pair(0 ,1),
            new Pair(-1, 1),
            new Pair(-1, 0),
            new Pair(0, -1)
    );

    static long naturalToInteger(long natural) {
        if (natural < 0) {
            return (-2 * natural) - 1;
        }
        return 2 * natural;
    }

    static long integerToNatural(long integer) {
        if (integer < 0) {
            throw new RuntimeException("Cannot be negative");
        }
        return integer % 2 == 0 ? integer / 2 : (integer + 1) / -2;
    }

    static long clusterID(HexPoint hexPoint) {
        return Szudzik.pair(naturalToInteger(hexPoint.q()), naturalToInteger(hexPoint.r()));
    }

    static HexPoint coordinate(long clusterID) {
        var pair = Szudzik.unpair(clusterID);
        return new HexPoint(integerToNatural(pair.a()), integerToNatural(pair.b()));
    }

    public List<ClusterID> neighbours() {
        return AXIAL_DIRECTION_VECTORS
                .stream()
                .map(directionPair -> this.coordinates.translate(directionPair.a(), directionPair.b()))
                .map(ClusterID::new)
                .collect(Collectors.toList());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterID clusterID = (ClusterID) o;
        return numeric == clusterID.numeric;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeric);
    }
}
