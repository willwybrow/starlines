package uk.wycor.starlines.web;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.wycor.starlines.domain.ClusterID;

import java.util.List;
import java.util.stream.Collectors;


@Getter
public class ClusterMetadataJson extends ClusterIDJson {
    final List<ClusterIDJson> neighbours;

    public ClusterMetadataJson(ClusterID clusterID) {
        super(clusterID);
        this.neighbours = clusterID
                .neighbours()
                .stream()
                .map(ClusterIDJson::new)
                .collect(Collectors.toList());
    }
}
