package uk.wycor.starlines.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.domain.geometry.Pair;
import uk.wycor.starlines.domain.geometry.Szudzik;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@JsonSerialize
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = ClusterID.class)
public class ClusterID {
    @JsonProperty("id")
    protected final long numeric;
    @JsonProperty("coordinates")
    protected final HexPoint coordinates;

    public ClusterID(long numeric) {
        this.numeric = Math.max(0L, numeric);
        this.coordinates = ClusterID.coordinate(this.numeric);
    }

    public ClusterID(HexPoint coordinates) {
        this.coordinates = coordinates;
        this.numeric = ClusterID.clusterID(coordinates);
    }

    public ClusterIDCluster withNeighbours() {
        return new ClusterIDCluster(this.coordinates);
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
