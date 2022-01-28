package uk.wycor.starlines.domain;

import uk.wycor.starlines.RandomSample;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Starfield {

    static final List<Point> ALL_POSSIBLE_CLUSTER_COORDINATES = IntStream
            .range(0, UniverseManager.CLUSTER_SIZE)
            .mapToObj(x -> IntStream.range(0, UniverseManager.CLUSTER_SIZE).boxed().map(y -> new Point(x, y)))
            .flatMap(p -> p)
            .toList();

    private static Map<Point, Star> generateRandomStarfield(int clusterID) {
        Random random = new Random();
        var totalMassToDistribute = UniverseManager.MASS_PER_NEW_CLUSTER;
        var newStarMasses = new ArrayList<Integer>();

        while (totalMassToDistribute >= 0) {
            var newStarMass = random.nextInt(UniverseManager.MASS_PER_NEW_CLUSTER / 2);
            newStarMasses.add(newStarMass);
            totalMassToDistribute -= newStarMass;
        }

        List<Point> randomPointsForNewStars = RandomSample.sample(ALL_POSSIBLE_CLUSTER_COORDINATES, newStarMasses.size());
        return IntStream.range(0, newStarMasses.size())
                .boxed()
                .collect(Collectors.toMap(
                        randomPointsForNewStars::get,
                        i -> new Star(
                                UUID.randomUUID(),
                                randomPointsForNewStars.get(i),
                                newStarMasses.get(i),
                                (int) Math.round((float)newStarMasses.get(i) * 1.75))
                        )
                );
    }
}
