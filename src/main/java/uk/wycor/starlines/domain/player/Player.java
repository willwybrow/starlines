package uk.wycor.starlines.domain.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import uk.wycor.starlines.domain.GameObject;
import uk.wycor.starlines.domain.ship.Harvester;
import uk.wycor.starlines.domain.ship.Probe;
import uk.wycor.starlines.domain.ship.Ship;
import uk.wycor.starlines.domain.ship.Stabiliser;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Node("Player")
public class Player extends GameObject {
    @JsonProperty
    private String name;

    @Relationship(type = "OWNED_BY", direction = INCOMING)
    private Set<Probe> ownedProbes;

    @Relationship(type = "OWNED_BY", direction = INCOMING)
    private Set<Harvester> ownedHarvesters;

    @Relationship(type = "OWNED_BY", direction = INCOMING)
    private Set<Stabiliser> ownedStabilisers;

    @Transient
    public Set<Ship> ownedShips() {
        return Stream
                .concat(ownedProbes.stream(), Stream.concat(ownedHarvesters.stream(), ownedStabilisers.stream()))
                .collect(Collectors.toSet());
    }

    public Player(UUID id, String name) {
        super(id);
        this.name = name;
        this.ownedProbes = new HashSet<>();
        this.ownedHarvesters = new HashSet<>();
        this.ownedStabilisers = new HashSet<>();
    }
}