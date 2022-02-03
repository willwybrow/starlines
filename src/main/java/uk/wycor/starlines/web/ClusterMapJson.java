package uk.wycor.starlines.web;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class ClusterMapJson {
    long id;
    Set<ClusterJson> clusters;
}
