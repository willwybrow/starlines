package uk.wycor.starlines.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClusterJson {
    List<StarJson> stars;
}
