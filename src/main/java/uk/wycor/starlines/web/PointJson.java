package uk.wycor.starlines.web;

import lombok.Builder;
import lombok.Data;
import uk.wycor.starlines.domain.Point;

@Data
@Builder
public class PointJson {
    private int x;
    private int y;

    public static PointJson fromPoint(Point point) {
        return PointJson.builder().x(point.x()).y(point.y()).build();
    }
}
