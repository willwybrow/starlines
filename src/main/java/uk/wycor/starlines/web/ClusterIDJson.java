package uk.wycor.starlines.web;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.wycor.starlines.domain.ClusterID;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
public class ClusterIDJson {
    final long id;
    final HexPointJson coordinates;

    public ClusterIDJson(ClusterID clusterID) {
        this(clusterID.getNumeric(), HexPointJson.from(clusterID.getCoordinates()));
    }
}
