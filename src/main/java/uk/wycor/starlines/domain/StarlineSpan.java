package uk.wycor.starlines.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import uk.wycor.starlines.domain.star.Star;

import java.util.UUID;

@Getter
@NoArgsConstructor
@RelationshipProperties
@SuperBuilder
public class StarlineSpan extends GameObjectRelationship {

    @TargetNode
    private Star star;

    @JsonProperty
    private UUID starlineID;

    @JsonProperty
    private long sequesteredMass;

    @Transient
    @JsonProperty
    public UUID starID() {
        return this.star.getId();
    }
}
