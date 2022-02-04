package uk.wycor.starlines.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.StarControl;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class ClusterJson {
    ClusterMetadataJson metadata;
    List<StarJson> stars;

    public static ClusterJson from(ClusterID clusterID, Map<HexPoint, StarControl> starControlMap) {
        return new ClusterJson(
                new ClusterMetadataJson(clusterID),
                starControlMap
                        .values()
                        .stream()
                        .map(StarJson::from)
                        .collect(Collectors.toList())
        );
    }
}
