package uk.wycor.starlines.domain;

import lombok.Getter;
import lombok.Setter;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.UUID;

@Getter
@Setter

public class Star extends GameObject {
    private final ClusterID location;
    private final HexPoint coordinate;

    private final String name;
    private final long naturalMassCapacity;

    private long currentMass;
    private long stabilisation;

    private long accumulatedInstability;

    public Star(UUID id, ClusterID location, HexPoint coordinate, String name, long currentMass, long naturalMassCapacity, long stabilisation, long accumulatedInstability) {
        super(id);
        this.location = location;
        this.coordinate = coordinate;
        this.name = name;
        this.currentMass = currentMass;
        this.naturalMassCapacity = naturalMassCapacity;
        this.stabilisation = stabilisation;
        this.accumulatedInstability = accumulatedInstability;
    }

    public long getMaximumMass() {
        return this.getNaturalMassCapacity() + this.getStabilisation();
    }

    public void loseMass(long mass) {
        this.currentMass = Math.max(0, this.currentMass - mass);
    }
}
