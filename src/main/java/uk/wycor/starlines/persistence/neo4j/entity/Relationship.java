package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.id.UuidStrategy;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@RelationshipEntity
public abstract class Relationship {
    @Id
    @GeneratedValue(strategy = UuidStrategy.class)
    @Convert(UuidStringConverter.class)
    final UUID id;

    public Relationship() {
        this.id = UUID.randomUUID();
    }
}
