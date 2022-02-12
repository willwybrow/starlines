package uk.wycor.starlines.domain.star;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.convert.ConvertWith;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.wycor.starlines.domain.GameObject;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.domain.ship.Probe;
import uk.wycor.starlines.domain.ship.Ship;
import uk.wycor.starlines.domain.ship.Stabiliser;
import uk.wycor.starlines.persistence.neo4j.ClusterIDConverter;
import uk.wycor.starlines.persistence.neo4j.HexPointConverter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@JsonInclude(value = NON_NULL)
@Node("Star")
public class Star extends GameObject {
    private static Logger logger = Logger.getLogger(Star.class.getName());

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
    private long stabilisation; // provided every tick by stabilisers executing the Stabilise order

    @JsonProperty
    private long accumulatedInstability;

    @Relationship(type = "LINKED_TO", direction = INCOMING)
    @Builder.Default
    private Set<Star> linkedFrom = new HashSet<>();

    @Relationship(type = "LINKED_TO", direction = OUTGOING)
    @Builder.Default
    private Set<Star> linkedTo = new HashSet<>();

    @Relationship(type = "ORBITING", direction = INCOMING)
    @Builder.Default
    @ReadOnlyProperty
    private Set<Probe> probesInOrbit = new HashSet<>();

    @Relationship(type = "ORBITING", direction = INCOMING)
    @Builder.Default
    @ReadOnlyProperty
    private Set<Harvester> harvestersInOrbit = new HashSet<>();

    @Relationship(type = "ORBITING", direction = INCOMING)
    @Builder.Default
    @ReadOnlyProperty
    private Set<Stabiliser> stabilisersInOrbit = new HashSet<>();

    public Star() {
        this.probesInOrbit = new HashSet<>();
        this.harvestersInOrbit = new HashSet<>();
        this.stabilisersInOrbit = new HashSet<>();
        this.linkedFrom = new HashSet<>();
        this.linkedTo = new HashSet<>();
    }

    @JsonProperty("clusterNumber")
    @Transient
    public long getClusterNumber() {
        return this.getClusterID().getNumeric();
    }

    @JsonProperty("maximumMass")
    @Transient
    public long calculateMaximumMass() {
        return this.getNaturalMassCapacity() + this.getStabilisation();
    }

    public void loseMass(long mass) {
        this.currentMass = Math.max(0, this.currentMass - mass);
    }

    public Star recalculateInstability() {
        /*
        For every unit mass this star is over its maximum, it accrues one unit of instability
        For every unit of stabilisation in excess of its current mass, it loses one unit of instability
         */
        this.accumulatedInstability = Math.max(0,
                this.accumulatedInstability + this.currentInstability()
        );
        return this;
    }

    private long currentInstability() {
        return this.getCurrentMass() - this.calculateMaximumMass();
    }

    public void harvestMass() {
        logger.info(String.format("Increasing %s (%s)'s mass by one!", this.getName(), this.getId().toString()));
        this.currentMass = Math.max(0, this.currentMass + 1);
    }

    @JsonProperty("control")
    @Transient
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
