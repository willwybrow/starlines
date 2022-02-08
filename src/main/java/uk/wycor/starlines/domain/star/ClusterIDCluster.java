package uk.wycor.starlines.domain.star;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.List;
import java.util.stream.Collectors;

public class ClusterIDCluster extends ClusterID {
    public ClusterIDCluster(long numeric) {
        super(numeric);
    }

    public ClusterIDCluster(HexPoint coordinates) {
        super(coordinates);
    }

    public ClusterID withoutNeighbours() {
        return this;
    }

    @JsonProperty("neighbours")
    public List<ClusterID> neighbours() {
        return AXIAL_DIRECTION_VECTORS
                .stream()
                .map(directionPair -> this.coordinates.translate(directionPair.a(), directionPair.b()))
                .map(ClusterID::new)
                .collect(Collectors.toList());
    }
}
