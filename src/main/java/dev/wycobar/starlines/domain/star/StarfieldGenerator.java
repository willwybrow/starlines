package dev.wycobar.starlines.domain.star;

import dev.wycobar.starlines.RandomSample;
import dev.wycobar.starlines.domain.StarNameGenerator;
import dev.wycobar.starlines.domain.geometry.HexPoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StarfieldGenerator {

    static final int CLUSTER_SUBDIVISIONS = 7;
    static final int MASS_PER_NEW_CLUSTER = CLUSTER_SUBDIVISIONS * 30;
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

    public static Set<Star> generateRandomStarfield(ClusterID destinationCluster) {
        Random random = new Random();
        var totalMassToDistribute = MASS_PER_NEW_CLUSTER;
        var newStarMasses = new ArrayList<Integer>();

        while (totalMassToDistribute >= 0 && newStarMasses.size() < ALL_POSSIBLE_CLUSTER_COORDINATES.size()) {
            var newStarMass = random.nextInt(MASS_PER_NEW_CLUSTER / MINIMUM_STAR_COUNT) + 1;
            newStarMasses.add(newStarMass);
            totalMassToDistribute -= newStarMass;
        }

        List<HexPoint> randomPointsForNewStars = RandomSample.sample(ALL_POSSIBLE_CLUSTER_COORDINATES, newStarMasses.size());
        return IntStream.range(0, newStarMasses.size())
                .boxed()
                .map(
                        i ->  Star
                                .builder()
                                .id(UUID.randomUUID())
                                .clusterID(destinationCluster)
                                .coordinates(randomPointsForNewStars.get(i))
                                .name(StarNameGenerator.randomName())
                                .currentMass(newStarMasses.get(i))
                                .naturalMassCapacity(Math.round((float) newStarMasses.get(i) * 1.75))
                                .accumulatedInstability(0)
                                .stabilisation(0)
                                .probesInOrbit(new HashSet<>())
                                .build()
                ).collect(Collectors.toSet());
    }
}
