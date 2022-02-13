package dev.wycobar.starlines.domain.ship.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Node;

@Node("DeployHarvester")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class DeployHarvesterOrder extends OneTimeOrder {

}
