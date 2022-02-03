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
    private final ClusterID location;
    private final HexPoint coordinate;

    private final String name;

    private final int currentMass;

    private final int naturalMassCapacity;

    private final int stabilisation;

    private final int accumulatedInstability;

    public Star(UUID id, ClusterID location, HexPoint coordinate, String name, int currentMass, int naturalMassCapacity, int stabilisation, int accumulatedInstability) {
        super(id);
        this.location = location;
        this.coordinate = coordinate;
        this.name = name;
        this.currentMass = currentMass;
        this.naturalMassCapacity = naturalMassCapacity;
        this.stabilisation = stabilisation;
        this.accumulatedInstability = accumulatedInstability;
    }

    public int getMaximumMass() {
        return this.getNaturalMassCapacity() + this.getStabilisation();
    }
}
