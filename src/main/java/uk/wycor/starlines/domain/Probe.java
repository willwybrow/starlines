package uk.wycor.starlines.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("Probe")
@Getter
@Setter
@JsonSerialize
public class Probe extends Ship {
    public Probe(UUID id, Player owner, Star orbiting) {
        super(id, owner, orbiting);
    }
}
