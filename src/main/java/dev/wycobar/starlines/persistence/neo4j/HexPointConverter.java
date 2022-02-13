package dev.wycobar.starlines.persistence.neo4j;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;
import dev.wycobar.starlines.domain.geometry.HexPoint;

public class HexPointConverter implements Neo4jPersistentPropertyConverter<HexPoint> {

    private static final int SRID = 9157;

    @Override
    public Value write(HexPoint source) {
        return Values.point(SRID, (double) source.q(), (double) source.r(), (double) source.s());
    }

    @Override
    public HexPoint read(Value source) {
        var point = source.asPoint();
        return new HexPoint((long) point.x(), (long) point.y());
    }
}
