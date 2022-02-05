package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.Map;

public class HexPointConverter implements Neo4jPersistentPropertyConverter<HexPoint>  {
    @Override
    public Value write(HexPoint source) {
        return Values.value(Map.of("q", source.q(), "r", source.r(), "s", source.s()));
    }

    @Override
    public HexPoint read(Value source) {
        var point = source.asPoint();
        return new HexPoint((long)point.x(), (long)point.y());
    }
}
