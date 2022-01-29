package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import uk.wycor.starlines.domain.Player;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity(label = "Player")
@Builder
public class PlayerEntity extends Entity {
    String name;

    @Relationship(type = "STARTED_AT")
    StarEntity startedAt;

    public static PlayerEntity fromPlayer(Player player, StarEntity startedAt) {
        return PlayerEntity.builder().name(player.getName()).startedAt(startedAt).build();
    }

    public Player toPlayer() {
        return new Player(this.id, this.name);
    }
}
