package uk.wycor.starlines.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StarlineLeg {
    private final Star starA;
    private final Star starB;
    private final long sequesteredMass;
}
