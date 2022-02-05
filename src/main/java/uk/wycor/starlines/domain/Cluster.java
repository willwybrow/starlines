package uk.wycor.starlines.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Cluster {
    @JsonProperty("metadata")
    private final ClusterID clusterID;

    @JsonProperty("stars")
    private final List<Star> stars;
}
