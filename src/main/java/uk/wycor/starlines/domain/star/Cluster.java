package uk.wycor.starlines.domain.star;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public class Cluster {
    @JsonProperty("metadata")
    private final ClusterID clusterID;

    @JsonProperty("stars")
    private final Set<Star> stars;
}
