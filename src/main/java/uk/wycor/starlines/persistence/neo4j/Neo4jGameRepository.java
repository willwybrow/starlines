package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.driver.Value;
import org.neo4j.driver.types.MapAccessor;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;
import org.neo4j.ogm.types.spatial.CartesianPoint3d;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.GameRepository;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.StarControl;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.NewPlayerWork;
import uk.wycor.starlines.persistence.neo4j.entity.PlayerEntity;
import uk.wycor.starlines.persistence.neo4j.entity.ProbeEntity;
import uk.wycor.starlines.persistence.neo4j.entity.StarEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Neo4jGameRepository implements GameRepository {
    protected Session ogmSession = Neo4jSessionFactory.getInstance().getNeo4jSession();

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
    public Set<StarControl> getClusterControllers(ClusterID clusterID) {
        return StreamSupport.stream(ogmSession.query("""
                        MATCH (star:Star) WHERE star.clusterID = $clusterID\s
                        OPTIONAL MATCH (star:Star)<-[o:ORBITING]-(ship:Probe)-[ob:OWNED_BY]->(player:Player)
                        WITH star, player, count(ship) AS playerProbes\s
                        WITH star, apoc.agg.maxItems(player, playerProbes) AS maxData\s
                        RETURN star, maxData.items AS controllingPlayers, maxData.value AS numberOfProbes""", Map.of("clusterID", clusterID.getNumeric()))
                .spliterator(), false)
                .map(result -> new StarControl(
                        ((StarEntity) result.get("star")).toStar(),
                        handleCustomQueryResultList(result.get("controllingPlayers"), PlayerEntity.class).stream().map(PlayerEntity::toPlayer).collect(Collectors.toList()),
                        handleCustomQueryResultScalar(result.get("numberOfProbes"))
                ))
                .collect(Collectors.toSet());
    }

    private <T> List<T> handleCustomQueryResultList(Object resultValue, Class<T> listItemClass) {
        try {
            return (List<T>) resultValue;
        } catch (ClassCastException | NullPointerException e) {
            return Collections.emptyList();
        }
    }

    private int handleCustomQueryResultScalar(Object castable){
        try {
            return (Integer) castable;
        } catch (ClassCastException | NullPointerException e) {
            return 0;
        }
    }

    private Star mapFromResult(MapAccessor star) {
        return new Star(
                UUID.fromString(star.get("id").asString()),
                new HexPoint((long)star.get("coordinate").asPoint().x(), (long)star.get("coordinate").asPoint().y()),
                star.get("name").asString(),
                star.get("currentMass").asInt(),
                star.get("maximumMass").asInt()
        );
    }

    private List<Player> mapFromListOfPlayerEntities(Value listOfPlayers) {
        return listOfPlayers.asList(value -> new Player(UUID.fromString(value.get("id").asString()), value.get("name").asString()));
    }

    @Override
    public ClusterID populateNextStarfield(Map<HexPoint, Star> starfield) {
        try (Transaction transaction = ogmSession.beginTransaction(Transaction.Type.READ_WRITE)) {
            var nextClusterID = nextClusterID();
            starfield.forEach((hexPoint, star) -> ogmSession.save(new StarEntity(nextClusterID.getNumeric(), new CartesianPoint3d(hexPoint.q(), hexPoint.r(), hexPoint.s()), star.getName(), star.getCurrentMass(), star.getMaximumMass(), Collections.emptySet())));
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

    private Iterable<StarEntity> starsInCluster(ClusterID clusterID) {
        return ogmSession.query(StarEntity.class, "MATCH (star:Star) " +
                "WHERE star.clusterID = $clusterID " +
                "RETURN star", Map.of("clusterID", clusterID.getNumeric()));
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
    public Collection<Star> bestStarsInCluster(ClusterID clusterID) {
        return StreamSupport
                .stream(ogmSession.query(StarEntity.class, "MATCH (star:Star) " +
                "WHERE star.clusterID = $clusterID " +
                "WITH apoc.agg.maxItems(star, star.currentMass) as maxData " +
                "RETURN maxData.items", Map.of("clusterID", clusterID.getNumeric())).spliterator(), false)
                .map(StarEntity::toStar)
                .collect(Collectors.toSet());
    }
}
