package uk.wycor.starlines.domain.order;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@Node("EstablishSelfAsHarvester")
@SuperBuilder
@NoArgsConstructor
public class EstablishSelfAsHarvester extends OneTimeOrder {

}
