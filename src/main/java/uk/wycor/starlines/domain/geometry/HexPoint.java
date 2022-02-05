package uk.wycor.starlines.domain.geometry;

import java.util.Objects;
import java.util.stream.Stream;

public record HexPoint(long q, long r) {
    public long s() {
        return (-this.q) - this.r;
    }

    public HexPoint translate(long deltaQ, long deltaR) {
        return new HexPoint(this.q + deltaQ, this.r + deltaR);
    }

    public Long distanceTo(HexPoint b) {
        var deltaQ = this.q() - b.q();
        var deltaR = this.r() - b.r();
        var deltaS = this.s() - b.s();

        return Stream.of(deltaQ, deltaR, deltaS).map(Math::abs).mapToLong(Long::longValue).sum() / 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexPoint hexPoint = (HexPoint) o;
        return q == hexPoint.q && r == hexPoint.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }
}
