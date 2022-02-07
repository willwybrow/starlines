package uk.wycor.starlines.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.convert.ConvertWith;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.neo4j.ClusterIDConverter;
import uk.wycor.starlines.persistence.neo4j.HexPointConverter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Node("Star")
public class Star extends GameObject {
    @ConvertWith(converter = ClusterIDConverter.class)
    private ClusterID clusterID;

    @ConvertWith(converter = HexPointConverter.class)
    @JsonProperty("coordinates")
    private HexPoint coordinates;

    @JsonProperty
    private String name;
    @JsonProperty
    private long naturalMassCapacity;

    @JsonProperty
    private long currentMass;
    @JsonProperty
    private long stabilisation;


    @JsonProperty
    private long accumulatedInstability;

    @Relationship(type = "LINKED_TO", direction = INCOMING)
    Set<Star> linkedFrom;

    @Relationship(type = "LINKED_TO", direction = OUTGOING)
    Set<Star> linkedTo;

    @Relationship(type = "ORBITING", direction = INCOMING)
    @JsonProperty
    @Builder.Default
    Set<Probe> probesInOrbit = new HashSet<>();

    public Star(UUID id, ClusterID clusterID, HexPoint coordinates, String name, long currentMass, long naturalMassCapacity, long stabilisation, long accumulatedInstability) {
        super(id);
        this.clusterID = clusterID;
        this.coordinates = coordinates;
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

    @JsonProperty("control")
    public StarControl getStarControl() {
        return Optional.ofNullable(this.getProbesInOrbit())
                .orElse(Collections.emptySet())
                .stream()
                .collect(Collectors.groupingBy(Ship::getOwner))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()))
                .values()
                .stream()
                .max(Integer::compare)
                .map(maxNumberOfShips -> new StarControl(
                        this.getProbesInOrbit()
                                .stream()
                                .collect(Collectors.groupingBy(Ship::getOwner))
                                .entrySet()
                                .stream()
                                .filter(entry -> entry.getValue().size() == maxNumberOfShips)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toSet()),
                        maxNumberOfShips
                )).orElse(new StarControl(Collections.emptyList(), 0));
    }
}
