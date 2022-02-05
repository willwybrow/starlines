package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.StarProbeOrbit;
import uk.wycor.starlines.domain.Starline;
import uk.wycor.starlines.domain.StarlineLeg;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.GameRepository;
import uk.wycor.starlines.persistence.NewPlayerWork;
import uk.wycor.starlines.persistence.neo4j.entity.PlayerEntity;
import uk.wycor.starlines.persistence.neo4j.entity.ProbeEntity;
import uk.wycor.starlines.persistence.neo4j.entity.StarEntity;
import uk.wycor.starlines.persistence.neo4j.entity.StarlineLink;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Neo4jGameRepository implements GameRepository {
    protected Session ogmSession = Neo4jSessionFactory.getInstance().getNeo4jSession();
    Logger logger = Logger.getLogger(Neo4jGameRepository.class.getName());

    @Override
    public Player setUpNewPlayer(NewPlayerWork newPlayerWork) {
        try (Transaction ogmTransaction = ogmSession.beginTransaction()) {
            var clusterId = newPlayerWork.destinationClusterPicker().get();
            var startingStar = newPlayerWork.starPicker().apply(newPlayerWork.getStarsInCluster().apply(clusterId));
            StarEntity starEntity = getStarEntity(startingStar);
            var newPlayer = newPlayerWork.newPlayerSupplier().get();
            logger.info(String.format("Picked cluster ID %d to save new player %s to", clusterId.getNumeric(), newPlayer.getName()));
            var newPlayerEntity = PlayerEntity.fromPlayer(newPlayer, starEntity);
            logger.info(String.format("Saving new player ID: %s name: %s", newPlayerEntity.getId(), newPlayerEntity.getName()));
            ogmSession.save(newPlayerEntity);
            ogmSession.save(newPlayerWork.startingProbeSupplier()
                    .get()
                    .stream()
                    .map(probe -> ProbeEntity
                            .builder()
                            .id(probe.getId())
                            .ownedBy(newPlayerEntity)
                            .orbiting(starEntity)
                            .build()
                    )
                    .peek(probeEntity -> logger.info(String.format("Created new probe %s belonging to player %s due to orbit star %s in cluster %d", probeEntity.getId(), probeEntity.getOwnedBy().getName(), probeEntity.getOrbiting().getName(), probeEntity.getOrbiting().getClusterID())))
                    .collect(Collectors.toSet()), 2);
            ogmTransaction.commit();
            return newPlayer;
        }
    }

    @Override
    public Set<StarProbeOrbit> getStarsAndOrbitingProbesInCluster(ClusterID clusterID) {
        return ogmSession
                .loadAll(StarEntity.class, new Filter("clusterID", ComparisonOperator.EQUALS, clusterID.getNumeric()), 3)
                .stream()
                .map(starEntity -> new StarProbeOrbit(starEntity.toStar(), starEntity.probesInOrbit()))
                .collect(Collectors.toSet());
    }

    @Override
    public Map<ClusterID, Set<StarProbeOrbit>> getStarsAndOrbitingProbesInClusters(Set<ClusterID> clusterIDs) {
        return ogmSession
                .loadAll(StarEntity.class, new Filter("clusterID", ComparisonOperator.IN, clusterIDs.stream().map(ClusterID::getNumeric).collect(Collectors.toSet())), 3)
                .stream()
                .map(starEntity -> new StarProbeOrbit(starEntity.toStar(), starEntity.probesInOrbit()))
                .collect(Collectors.groupingBy(starProbeOrbit -> starProbeOrbit.getStar().getLocation(), Collectors.toSet()));
    }

    @Override
    public ClusterID populateNextStarfield(Function<ClusterID, Map<HexPoint, Star>> starfieldGenerator) {
        try (Transaction transaction = ogmSession.beginTransaction(Transaction.Type.READ_WRITE)) {
            var nextClusterID = nextClusterID();
            starfieldGenerator.apply(nextClusterID).values().stream().map(StarEntity::from).forEach(ogmSession::save);
            transaction.commit();
            return nextClusterID;
        }
    }

    @Override
    public ClusterID pickUnoccupiedCluster() {
        logger.info("Trying to find an unoccupied cluster to start a new player in...");
        try {
            Integer pickedClusterID = ogmSession.queryForObject(Integer.class,
                    """ 
                            MATCH (star:Star) OPTIONAL MATCH (star:Star)<-[o:ORBITING]-(ship:Probe) WITH star.clusterID as clusterID, count(star) AS starsInCluster, count(ship) AS shipsInCluster WHERE starsInCluster > 0 AND shipsInCluster = 0 RETURN clusterID LIMIT 1;
                            """,
                    Collections.emptyMap()
            );
            logger.info(String.format("We think that cluster %d is empty.", pickedClusterID));
            return new ClusterID(pickedClusterID);
        } catch (NullPointerException | NoSuchElementException e) {
            return nextClusterID(); // beginning of game
        }
    }

    @Override
    public Collection<Star> getStarsInCluster(ClusterID clusterID) {
        return ogmSession.loadAll(StarEntity.class, new Filter("clusterID", ComparisonOperator.EQUALS, clusterID.getNumeric()), 0).stream().map(StarEntity::toStar).collect(Collectors.toSet());
    }

    @Override
    public Collection<Starline> getStarlinesInUniverse() {
        return ogmSession.loadAll(StarlineLink.class, 1)
                .stream()
                .collect(Collectors.groupingBy(StarlineLink::getStarlineID))
                .entrySet()
                .stream()
                .map(entry -> new Starline(
                        entry.getKey(),
                        entry
                                .getValue()
                                .stream()
                                .map(starlineLink -> new StarlineLeg(
                                        starlineLink
                                                .getLinkTo()
                                                .toStar(),
                                        starlineLink
                                                .getLinkFrom()
                                                .toStar(),
                                        starlineLink
                                                .getSequesteredMass()
                                ))
                                .collect(Collectors.toSet())
                )).collect(Collectors.toList());
    }

    public Starline getStarline(UUID id) {
        return new Starline(id, ogmSession.loadAll(StarlineLink.class, new Filter("starlineID", ComparisonOperator.EQUALS, id), 1)
                .stream()
                .map(starlineLink -> new StarlineLeg(starlineLink.getLinkFrom().toStar(), starlineLink.getLinkTo().toStar(), starlineLink.getSequesteredMass()))
                .collect(Collectors.toSet())
        );
    }

    @Override
    public void deleteStarline(Starline starline) {
        ogmSession.delete(
                StarlineLink.class,
                Collections.singleton(new Filter("starlineID", ComparisonOperator.EQUALS, starline.getId())),
                false
        );
    }

    @Override
    public Starline saveStarline(Starline newStarline) {
        ogmSession.save(
                newStarline.getNetwork().stream().map(starlineLeg -> StarlineLink
                        .builder()
                        .starlineID(newStarline.getId())
                        .linkFrom(StarEntity.from(starlineLeg.getStarA()))
                        .linkTo(StarEntity.from(starlineLeg.getStarB()))
                        .sequesteredMass(starlineLeg.getSequesteredMass())
                        .build()
                ).collect(Collectors.toSet())
        );
        return getStarline(newStarline.getId());
    }

    private Optional<ClusterID> latestGeneratedCluster() {
        try {
            return Optional.of(ogmSession.query(Integer.class, "MATCH (star:Star) RETURN max(star.clusterID) AS latestClusterID", Collections.emptyMap()).iterator().next()).map(ClusterID::new);
        } catch (NullPointerException | NoSuchElementException e) {
            return Optional.empty(); // beginning of game
        }
    }

    private StarEntity getStarEntity(Star star) {
        return ogmSession.load(StarEntity.class, star.getId());
    }

    private ClusterID nextClusterID() {
        logger.info("Getting the next cluster ID to be generated");
        return latestGeneratedCluster().map(ClusterID::getNumeric).map(i -> i + 1).map(ClusterID::new).orElse(new ClusterID(0));
    }
}
