package uk.wycor.starlines.web;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class StarlineJson {
    String id;
    List<StarlineLegJson> spans;
}
