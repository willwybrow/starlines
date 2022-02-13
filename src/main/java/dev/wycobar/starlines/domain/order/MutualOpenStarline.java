package dev.wycobar.starlines.domain.order;

import lombok.Getter;
import dev.wycobar.starlines.domain.ship.order.starline.OpenStarline;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Getter
public class MutualOpenStarline {
    OpenStarline a;
    OpenStarline b;

    public MutualOpenStarline(OpenStarline a, OpenStarline b) {
        this.a = a;
        this.b = b;
    }

    public Stream<OpenStarline> stream() {
        return this.asSet().stream();
    }

    private Set<OpenStarline> asSet() {
        return Set.of(a, b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutualOpenStarline that = (MutualOpenStarline) o;
        return Objects.equals(this.asSet(), that.asSet()) && Objects.equals(this.asSet(), that.asSet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.asSet());
    }
}
