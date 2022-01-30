package uk.wycor.starlines.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ClusterMetadataJson {
    HexPointJson coordinates;
    List<Long> neighbours;
}
