package uk.wycor.starlines.domain.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@Node("BuildHarvester")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class BuildHarvester extends Build {
}
