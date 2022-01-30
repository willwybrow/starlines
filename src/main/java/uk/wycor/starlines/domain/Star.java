package uk.wycor.starlines.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Star extends GameObject {

    private final HexPoint coordinate;

    private final String name;

    private final int currentMass;
    private final int maximumMass;

    public Star(UUID id, HexPoint coordinate, String name, int currentMass, int maximumMass) {
        super(id);
        this.coordinate = coordinate;
        this.name = name;
        this.currentMass = currentMass;
        this.maximumMass = maximumMass;
    }
}
