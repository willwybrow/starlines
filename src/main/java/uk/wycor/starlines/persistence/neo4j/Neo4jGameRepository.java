package uk.wycor.starlines.persistence.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.driver.Value;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;
import org.neo4j.ogm.types.spatial.CartesianPoint3d;
import uk.wycor.starlines.RandomSample;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.GameRepository;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.neo4j.entity.PlayerEntity;
import uk.wycor.starlines.persistence.neo4j.entity.ProbeEntity;
import uk.wycor.starlines.persistence.neo4j.entity.StarEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.wycor.starlines.persistence.neo4j.Neo4jDriver.DRIVER;

public class Neo4jGameRepository implements GameRepository {
    protected Session ogmSession = Neo4jSessionFactory.getInstance().getNeo4jSession();
    protected org.neo4j.driver.Session session = DRIVER.session();

    Iterable<StarEntity> loadAllStars() {
        return ogmSession.loadAll(StarEntity.class);
    }

    @Override
    public Player setUpNewPlayer(Player player) {
        try (Transaction transaction = ogmSession.beginTransaction()) {
            // TODO tomorrow: pick a cluster and have their first ship(s) orbit the best star of it
            var clusterId = pickUnoccupiedCluster();
            StarEntity startingStar = RandomSample.pick(bestStarsInCluster(clusterId));
            PlayerEntity playerEntity = PlayerEntity.fromPlayer(player, startingStar);
            ogmSession.save(playerEntity);
            ogmSession.save(ProbeEntity.builder().orbiting(startingStar).ownedBy(playerEntity).build());
            transaction.commit();
        }
        return player;
    }

    private Star mapFromResult(Value star) {
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
    public Map<Star, List<Player>> getClusterControllers(ClusterID clusterID) {
        return session.run("""
                        MATCH (star:Star) WHERE star.clusterID = $clusterID\s
                        OPTIONAL MATCH (star:Star)<-[o:ORBITING]-(ship:Probe)-[ob:OWNED_BY]->(player:Player)
                        WITH star, player, count(ship) AS playerProbes\s
                        WITH star, apoc.agg.maxItems(player, playerProbes) AS maxData\s
                        RETURN star, maxData.items AS controllingPlayers, maxData.value AS numberOfProbes""", Map.of("clusterID", clusterID.getNumeric()))
                .stream()
                .collect(Collectors.toMap(record -> mapFromResult(record.get("star")), record -> mapFromListOfPlayerEntities(record.get("controllingPlayers"))));
        /*
        Result result = ogmSession.query("""
                        MATCH (star:Star) WHERE star.clusterID = $clusterID\s
                        OPTIONAL MATCH (star:Star)<-[o:ORBITING]-(ship:Probe)-[ob:OWNED_BY]->(player:Player)
                        WITH star, player, count(ship) AS playerProbes\s
                        WITH star, apoc.agg.maxItems(player, playerProbes) AS maxData\s
                        RETURN star, maxData.items AS controllingPlayers, maxData.value AS numberOfProbes""", Map.of("clusterID", clusterID.getNumeric()));
        return StreamSupport.stream(result.spliterator(), false)
                .map(resultMap -> {
                    StarEntity starEntity = (StarEntity) resultMap.get("star");
                    try {
                        return new ControlResult(starEntity, (List<PlayerEntity>) resultMap.get("controllingPlayers"), (Integer) resultMap.get("numberOfProbes"));
                    } catch (ClassCastException e) {
                        return new ControlResult(starEntity, Collections.emptyList(), 0);
                    }
                }).collect(Collectors.toMap(controlResult -> controlResult.star.toStar(), controlResult -> controlResult.controllingPlayers.stream().map(PlayerEntity::toPlayer).collect(Collectors.toList())));
                */
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ControlResult {
        StarEntity star;
        List<PlayerEntity> controllingPlayers;
        Integer numberOfProbes;
    }

    public ClusterID populateNextStarfield(Map<HexPoint, Star> starfield) {
        try (Transaction transaction = ogmSession.beginTransaction(Transaction.Type.READ_WRITE)) {
            var nextClusterID = nextClusterID();
            starfield.forEach((hexPoint, star) -> ogmSession.save(new StarEntity(nextClusterID.getNumeric(), new CartesianPoint3d(hexPoint.q(), hexPoint.r(), hexPoint.s()), star.getName(), star.getCurrentMass(), star.getMaximumMass(), Collections.emptySet())));
            transaction.commit();
            return nextClusterID;
        }
    }

    private ClusterID pickUnoccupiedCluster() {
        try {
            return new ClusterID(ogmSession.query(Integer.class, "MATCH (star:Star)<-[o:ORBITING]-(ship:Ship) WITH star.clusterID as clusterID, count(ship) AS shipsInCluster WHERE shipsInCluster = 0 RETURN clusterID;", Collections.emptyMap()).iterator().next());
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

    private ClusterID nextClusterID() {
        return latestGeneratedCluster().map(ClusterID::getNumeric).map(i -> i + 1).map(ClusterID::new).orElse(new ClusterID(0));
    }

    private Iterable<StarEntity> bestStarsInCluster(ClusterID clusterID) {
        return ogmSession.query(StarEntity.class, "MATCH (star:Star) " +
                "WHERE star.clusterID = $clusterID " +
                "WITH apoc.agg.maxItems(star, star.currentMass) as maxData " +
                "RETURN maxData.items", Map.of("clusterID", clusterID.getNumeric()));
    }
}
