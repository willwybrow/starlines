package uk.wycor.starlines.domain.ship.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@Node("BuildProbe")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class BuildProbe extends Build {
}
