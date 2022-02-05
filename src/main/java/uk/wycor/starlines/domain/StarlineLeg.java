package uk.wycor.starlines.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public class StarlineLeg {
    private final Star starA;
    private final Star starB;
    private final long sequesteredMass;

    public Stream<Star> getBothStars() {
        return Stream.of(getStarA(), getStarB());
    }
}
