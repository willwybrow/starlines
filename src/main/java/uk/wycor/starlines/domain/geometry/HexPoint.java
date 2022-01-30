package uk.wycor.starlines.domain.geometry;

public record HexPoint(int q, int r) {
    public int s() {
        return (-this.q) - this.r;
    }
}
