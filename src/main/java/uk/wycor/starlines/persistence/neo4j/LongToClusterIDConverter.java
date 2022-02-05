package uk.wycor.starlines.persistence.neo4j;

import org.springframework.core.convert.converter.Converter;
import uk.wycor.starlines.domain.ClusterID;

public class LongToClusterIDConverter implements Converter<Long, ClusterID> {
    @Override
    public ClusterID convert(Long source) {
        return new ClusterID(source);
    }
}
