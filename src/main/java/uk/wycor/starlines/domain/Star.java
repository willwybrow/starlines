package uk.wycor.starlines.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.convert.ConvertWith;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.neo4j.ClusterIDConverter;
import uk.wycor.starlines.persistence.neo4j.HexPointConverter;

import java.util.Set;
import java.util.UUID;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@Node("Star")
public class Star extends GameObject {
    @ConvertWith(converter = ClusterIDConverter.class)
    private final ClusterID clusterID;
    @ConvertWith(converter = HexPointConverter.class)
    @JsonProperty("coordinate")
    private final HexPoint coordinate;

    @JsonProperty
    private final String name;
    @JsonProperty
    private final long naturalMassCapacity;

    @JsonProperty
    private long currentMass;
    @JsonProperty
    private long stabilisation;

    @JsonProperty
    private long accumulatedInstability;

    @Relationship(type = "LINKED_TO", direction = INCOMING)
    Set<Starline> linkedFrom;

    @Relationship(type = "LINKED_TO", direction = OUTGOING)
    Set<Starline> linkedTo;

    @Relationship(type = "ORBITING", direction = INCOMING)
    @JsonProperty
    Set<Probe> probesInOrbit;


    public Star(UUID id, ClusterID clusterID, HexPoint coordinate, String name, long currentMass, long naturalMassCapacity, long stabilisation, long accumulatedInstability) {
        super(id);
        this.clusterID = clusterID;
        this.coordinate = coordinate;
        this.name = name;
        this.currentMass = currentMass;
        this.naturalMassCapacity = naturalMassCapacity;
        this.stabilisation = stabilisation;
        this.accumulatedInstability = accumulatedInstability;
    }

    @JsonProperty("maximumMass")
    @Transient
    public long getMaximumMass() {
        return this.getNaturalMassCapacity() + this.getStabilisation();
    }

    public void loseMass(long mass) {
        this.currentMass = Math.max(0, this.currentMass - mass);
    }
}
