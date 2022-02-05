package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.internal.value.IntegerValue;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;
import uk.wycor.starlines.domain.ClusterID;

import java.util.Optional;

public class ClusterIDConverter implements Neo4jPersistentPropertyConverter<ClusterID> {

    @Override
    public Value write(ClusterID source) {
        return Optional.ofNullable(source).map(ClusterID::getNumeric).map(IntegerValue::new).orElse((IntegerValue) Values.NULL);
    }

    @Override
    public ClusterID read(Value source) {
        return new ClusterID(source.asNumber().longValue());
    }
}
