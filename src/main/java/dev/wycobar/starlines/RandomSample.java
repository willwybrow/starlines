package dev.wycobar.starlines;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSample {

    public static final Random RANDOM = new Random();

    public static <T> T pick(Iterable<T> items) {
        return sample(items, 1).get(0);
    }

    public static <T> List<T> sample(Iterable<T> items, int sampleSize) {
        ArrayList<T> reservoir = new ArrayList<>(sampleSize);
        int count = 0;
        for (T item : items) {
            count++;
            if (count <= sampleSize) {
                reservoir.add(item);
            } else {
                int r = RANDOM.nextInt(count);
                if (r < sampleSize) {
                    reservoir.set(r, item);
                }
            }
        }
        return reservoir;
    }
}
