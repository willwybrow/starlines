package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.id.UuidStrategy;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@SuperBuilder
abstract class Entity {
    @Id
    @GeneratedValue(strategy = UuidStrategy.class)
    @Convert(UuidStringConverter.class)
    final UUID id;

    public Entity() {
        this.id = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
