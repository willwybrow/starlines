package uk.wycor.starlines.web;

import lombok.Getter;
import uk.wycor.starlines.domain.geometry.HexPoint;

@Getter
public class HexPointJson {
    private final long q;
    private final long r;
    private final long s;

    HexPointJson(long q, long r, long s) {
        this.q = q;
        this.r = r;
        this.s = s;
    }

    public static HexPointJson from(HexPoint hexPoint) {
        return new HexPointJson(hexPoint.q(), hexPoint.r(), hexPoint.s());
    }
}
