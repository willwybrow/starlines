package uk.wycor.starlines.domain.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Build")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public abstract class Build extends RepeatableOrder {
}
