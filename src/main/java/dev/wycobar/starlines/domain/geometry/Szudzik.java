package dev.wycobar.starlines.domain.geometry;

import java.math.BigInteger;

public class Szudzik {

    public static long pair(long a, long b) {
        return a >= b ? a * a + a + b : b * b + a;
    }

    public static Pair unpair(long z) {
        long b = BigInteger.valueOf(z).sqrt().longValue();
        long a = z - b * b;
        return a < b ? new Pair(a, b) : new Pair(b, a - b);
    }
}
