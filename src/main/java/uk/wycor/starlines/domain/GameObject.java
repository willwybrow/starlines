package uk.wycor.starlines.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class GameObject {
    @Id
    @GeneratedValue(generatorClass = GeneratedValue.UUIDGenerator.class)
    UUID id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameObject that = (GameObject) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
