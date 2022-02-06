package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.PointValue;
import org.neo4j.driver.types.Point;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;
import org.springframework.data.neo4j.types.CartesianPoint3d;
import uk.wycor.starlines.domain.geometry.HexPoint;

public class HexPointConverter implements Neo4jPersistentPropertyConverter<HexPoint> {

    static class HexPoint3d implements Point {

        private final CartesianPoint3d cartesianPoint3d;

        public HexPoint3d(double x, double y, double z) {
            this.cartesianPoint3d = new CartesianPoint3d(x, y, z);
        }

        @Override
        public int srid() {
            return cartesianPoint3d.getSrid();
        }

        @Override
        public double x() {
            return cartesianPoint3d.getX();
        }

        @Override
        public double y() {
            return cartesianPoint3d.getY();
        }

        @Override
        public double z() {
            return cartesianPoint3d.getZ();
        }
    }

    @Override
    public Value write(HexPoint source) {
        var cartesianPoint = new HexPoint3d((double) source.q(), (double) source.r(), (double) source.s());
        return new PointValue(cartesianPoint);
    }

    @Override
    public HexPoint read(Value source) {
        var point = source.asPoint();
        return new HexPoint((long) point.x(), (long) point.y());
    }
}
