package uk.wycor.starlines.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Star extends GameObject {

    private final Point coordinate;

    private final String name;

    private final int currentMass;
    private final int maximumMass;

    public Star(UUID id, Point coordinate, String name, int currentMass, int maximumMass) {
        super(id);
        this.coordinate = coordinate;
        this.name = name;
        this.currentMass = currentMass;
        this.maximumMass = maximumMass;
    }
}
