package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.GameRepository;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.StarProbeOrbit;
import uk.wycor.starlines.domain.Starline;
import uk.wycor.starlines.domain.StarlineLeg;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.NewPlayerWork;
import uk.wycor.starlines.persistence.neo4j.entity.PlayerEntity;
import uk.wycor.starlines.persistence.neo4j.entity.ProbeEntity;
import uk.wycor.starlines.persistence.neo4j.entity.StarEntity;
import uk.wycor.starlines.persistence.neo4j.entity.StarlineLink;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
            var newPlayerEntity = PlayerEntity.fromPlayer(newPlayer, starEntity);
            ogmSession.save(newPlayerEntity);
            newPlayerWork.startingProbeSupplier().get()
                    .forEach(probe -> ogmSession.save(ProbeEntity.builder().id(probe.getId()).orbiting(starEntity).ownedBy(newPlayerEntity).build()));
            ogmTransaction.commit();
            return newPlayer;
        }
    }

    @Override
    public Set<StarProbeOrbit> getStarsAndOrbitingProbesInCluster(ClusterID clusterID) {
        return ogmSession
                .loadAll(StarEntity.class, new Filter("clusterID", ComparisonOperator.EQUALS, clusterID.getNumeric()), 3)
                .stream()
                .map(starEntity -> new StarProbeOrbit(starEntity.toStar(), starEntity.shipsInOrbit()))
                .collect(Collectors.toSet());
    }

    @Override
    public Map<ClusterID, Set<StarProbeOrbit>> getStarsAndOrbitingProbesInClusters(Set<ClusterID> clusterIDs) {
        return ogmSession
                .loadAll(StarEntity.class, new Filter("clusterID", ComparisonOperator.IN, clusterIDs.stream().map(ClusterID::getNumeric).collect(Collectors.toSet())), 3)
                .stream()
                .map(starEntity -> new StarProbeOrbit(starEntity.toStar(), starEntity.shipsInOrbit()))
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
        try {
            return new ClusterID(
                    ogmSession.query(Integer.class,
                            """ 
                                    MATCH (star:Star) OPTIONAL MATCH (star:Star)<-[o:ORBITING]-(ship:Ship) WITH star.clusterID as clusterID, count(star) AS starsInCluster, count(ship) AS shipsInCluster WHERE starsInCluster > 0 AND shipsInCluster = 0 RETURN clusterID;
                                    """,
                            Collections.emptyMap()
                    ).iterator().next());
        } catch (NullPointerException | NoSuchElementException e) {
            return nextClusterID(); // beginning of game
        }
    }

    @Override
    public Collection<Star> getStarsInCluster(ClusterID clusterID) {
        return StreamSupport
                .stream(ogmSession.query(StarEntity.class, "MATCH (star:Star) " +
                        "WHERE star.clusterID = $clusterID " +
                        "RETURN star", Map.of("clusterID", clusterID.getNumeric())).spliterator(), false)
                .map(StarEntity::toStar)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Starline> getStarlinesInUniverse() {
        return ogmSession.loadAll(StarlineLink.class)
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
        return latestGeneratedCluster().map(ClusterID::getNumeric).map(i -> i + 1).map(ClusterID::new).orElse(new ClusterID(0));
    }

    private <T> List<T> handleCustomQueryResultList(Object resultValue, Class<T> listItemClass) {
        try {
            return (List<T>) resultValue;
        } catch (ClassCastException | NullPointerException e) {
            return Collections.emptyList();
        }
    }

    private long handleCustomQueryResultScalar(Object castable){
        try {
            return (Long) castable;
        } catch (ClassCastException | NullPointerException e) {
            return 0;
        }
    }
}
