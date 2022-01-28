package uk.wycor.starlines;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomSample {

    public static final Random RANDOM = new Random();

    public static <T> List<T> sample(Iterable<T> items, int sampleSize) {
        ArrayList<T> reservoir = new ArrayList<T>(sampleSize);
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
