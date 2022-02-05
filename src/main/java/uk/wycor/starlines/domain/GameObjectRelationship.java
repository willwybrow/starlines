package uk.wycor.starlines.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@EqualsAndHashCode
@Getter
@Setter
@RelationshipProperties
@AllArgsConstructor
@NoArgsConstructor
public abstract class GameObjectRelationship {
    @GeneratedValue
    @RelationshipId
    Long id;

}
