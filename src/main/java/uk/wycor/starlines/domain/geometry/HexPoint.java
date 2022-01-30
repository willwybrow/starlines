package uk.wycor.starlines.domain.geometry;

public record HexPoint(long q, long r) {
    public long s() {
        return (-this.q) - this.r;
    }

    public HexPoint translate(long deltaQ, long deltaR) {
        return new HexPoint(this.q + deltaQ, this.r + deltaR);
    }
}
