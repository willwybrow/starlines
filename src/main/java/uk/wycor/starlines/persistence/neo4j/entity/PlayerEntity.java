package uk.wycor.starlines.persistence.neo4j.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import uk.wycor.starlines.domain.Player;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@NodeEntity(label = "Player")
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
