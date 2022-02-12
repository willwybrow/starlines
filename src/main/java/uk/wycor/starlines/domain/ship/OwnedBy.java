package uk.wycor.starlines.domain.ship;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import uk.wycor.starlines.domain.GameObjectRelationship;
import uk.wycor.starlines.domain.player.Player;

@RelationshipProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnedBy extends GameObjectRelationship {
    @TargetNode
    public Player player;
}
