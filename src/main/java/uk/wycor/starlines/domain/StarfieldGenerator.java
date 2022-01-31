package uk.wycor.starlines.domain;

import uk.wycor.starlines.RandomSample;
import uk.wycor.starlines.domain.geometry.HexPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StarfieldGenerator {

    static final int CLUSTER_SUBDIVISIONS = 7;
    static final int MASS_PER_NEW_CLUSTER = CLUSTER_SUBDIVISIONS * 3;
    static final int MINIMUM_STAR_COUNT = 3;

    static final List<HexPoint> ALL_POSSIBLE_CLUSTER_COORDINATES = IntStream
            .range(-(CLUSTER_SUBDIVISIONS / 2), (CLUSTER_SUBDIVISIONS / 2) + 1)
            .mapToObj(q -> IntStream.range(-(CLUSTER_SUBDIVISIONS / 2), (CLUSTER_SUBDIVISIONS / 2) + 1)
                    .boxed()
                    .map(r -> new HexPoint(q, r))
            )
            .flatMap(p -> p)
            .filter(hexPoint -> -(CLUSTER_SUBDIVISIONS / 2) <= hexPoint.s() && hexPoint.s() <= (CLUSTER_SUBDIVISIONS / 2))
            .toList();

    static Map<HexPoint, Star> generateRandomStarfield() {
        Random random = new Random();
        var totalMassToDistribute = MASS_PER_NEW_CLUSTER;
        var newStarMasses = new ArrayList<Integer>();

        while (totalMassToDistribute >= 0) {
            var newStarMass = random.nextInt(MASS_PER_NEW_CLUSTER / MINIMUM_STAR_COUNT) + 1;
            newStarMasses.add(newStarMass);
            totalMassToDistribute -= newStarMass;
        }

        List<HexPoint> randomPointsForNewStars = RandomSample.sample(ALL_POSSIBLE_CLUSTER_COORDINATES, newStarMasses.size());
        return IntStream.range(0, newStarMasses.size())
                .boxed()
                .collect(Collectors.toMap(
                        randomPointsForNewStars::get,
                        i -> new Star(
                                UUID.randomUUID(),
                                randomPointsForNewStars.get(i),
                                StarNameGenerator.randomName(),
                                newStarMasses.get(i),
                                (int) Math.round((float)newStarMasses.get(i) * 1.75))
                        )
                );
    }
}
