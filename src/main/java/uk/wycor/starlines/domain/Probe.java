package uk.wycor.starlines.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("Probe")
@JsonSerialize
public class Probe extends Ship {
    public Probe(UUID id, Player ownedBy) {
        super(id, ownedBy);
    }
}
