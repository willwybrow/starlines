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

    private int currentMass;
    private int maximumMass;

    public Star(UUID id, Point coordinate, int currentMass, int maximumMass) {
        super(id);
        this.coordinate = coordinate;
        this.currentMass = currentMass;
        this.maximumMass = maximumMass;
    }
}
