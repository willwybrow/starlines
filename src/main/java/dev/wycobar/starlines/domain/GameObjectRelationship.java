package dev.wycobar.starlines.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@EqualsAndHashCode
@Getter
@Setter
@RelationshipProperties
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class GameObjectRelationship {
    @RelationshipId
    Long id;

}
