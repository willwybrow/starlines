package uk.wycor.starlines.domain.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@Node("EstablishSelfAsHarvester")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class EstablishSelfAsHarvester extends OneTimeOrder {

}
