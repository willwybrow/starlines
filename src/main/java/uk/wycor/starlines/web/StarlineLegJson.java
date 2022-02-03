package uk.wycor.starlines.web;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StarlineLegJson {
    StarJson fromStar;
    StarJson toStar;
    long sequesteredMass;
}
