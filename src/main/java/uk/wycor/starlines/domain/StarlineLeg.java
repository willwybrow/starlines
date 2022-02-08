package uk.wycor.starlines.domain;

import lombok.Getter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import uk.wycor.starlines.domain.star.Star;

import java.util.stream.Stream;

@Getter
@RelationshipProperties()
public class StarlineLeg extends GameObjectRelationship {

    private final Star fromStar;
    @TargetNode
    private final Star toStar;
    private final long sequesteredMass;

    public StarlineLeg(Star fromStar, Star toStar, long sequesteredMass) {
        this.fromStar = fromStar;
        this.toStar = toStar;
        this.sequesteredMass = sequesteredMass;
    }

    @Transient
    public Stream<Star> getBothStars() {
        return Stream.of(this.getFromStar(), this.getToStar());
    }
}
