package uk.wycor.starlines.web;

import lombok.Getter;
import uk.wycor.starlines.domain.geometry.HexPoint;

@Getter
public class HexPointJson {
    private final int q;
    private final int r;
    private final int s;

    HexPointJson(int q, int r, int s) {
        this.q = q;
        this.r = r;
        this.s = s;
    }

    public static HexPointJson from(HexPoint hexPoint) {
        return new HexPointJson(hexPoint.q(), hexPoint.r(), hexPoint.s());
    }
}
