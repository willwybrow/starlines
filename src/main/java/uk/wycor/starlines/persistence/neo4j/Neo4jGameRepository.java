package uk.wycor.starlines.persistence.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.model.Result;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Neo4jGameRepository implements GameRepository {
    protected Session session = Neo4jSessionFactory.getInstance().getNeo4jSession();

    public static void main(String[] args) {
        Neo4jGameRepository neo4JGameRepository = new Neo4jGameRepository();
        System.out.println("Loading all stars...");
        neo4JGameRepository.loadAllStars().forEach(star -> System.out.println(star.getName()));
    }

    Iterable<StarEntity> loadAllStars() {
        return session.loadAll(StarEntity.class);
    }

    @Override
    public Player setUpNewPlayer(Player player) {
        try (Transaction transaction = session.beginTransaction()) {
            // TODO tomorrow: pick a cluster and have their first ship(s) orbit the best star of it
            int clusterId = pickUnoccupiedCluster();
            StarEntity startingStar = RandomSample.pick(bestStarsInCluster(clusterId));
            PlayerEntity playerEntity = PlayerEntity.fromPlayer(player, startingStar);
            session.save(playerEntity);
            session.save(ProbeEntity.builder().orbiting(startingStar).ownedBy(playerEntity).build());
            transaction.commit();
        }
        return player;
    }

    @Override
    public Map<Star, List<Player>> getClusterControllers(ClusterID clusterID) {
        Result result = session.query("""
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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ControlResult {
        StarEntity star;
        List<PlayerEntity> controllingPlayers;
        Integer numberOfProbes;
    }

    public int populateNextStarfield(Map<HexPoint, Star> starfield) {
        try (Transaction transaction = session.beginTransaction(Transaction.Type.READ_WRITE)) {
            int nextClusterID = latestGeneratedCluster() + 1;
            starfield.forEach((hexPoint, star) -> session.save(new StarEntity(nextClusterID, new CartesianPoint3d(hexPoint.q(), hexPoint.r(), hexPoint.s()), star.getName(), star.getCurrentMass(), star.getMaximumMass(), Collections.emptySet())));
            transaction.commit();
            return nextClusterID;
        }
    }

    private int pickUnoccupiedCluster() {
        try {
            return session.query(Integer.class, "MATCH (star:Star)<-[o:ORBITING]-(ship:Ship) WITH star.clusterID as clusterID, count(ship) AS shipsInCluster WHERE shipsInCluster = 0 RETURN clusterID;", Collections.emptyMap()).iterator().next();
        } catch (NullPointerException | NoSuchElementException e) {
            return latestGeneratedCluster(); // beginning of game
        }
    }

    private int latestGeneratedCluster() {
        try {
            return session.query(Integer.class, "MATCH (star:Star) RETURN max(star.clusterID) AS latestClusterID", Collections.emptyMap()).iterator().next();
        } catch (NullPointerException | NoSuchElementException e) {
            return -1; // beginning of game
        }
    }

    private Iterable<StarEntity> bestStarsInCluster(int clusterID) {
        return session.query(StarEntity.class, "MATCH (star:Star) " +
                "WHERE star.clusterID = $clusterID " +
                "WITH apoc.agg.maxItems(star, star.currentMass) as maxData " +
                "RETURN maxData.items", Map.of("clusterID", clusterID));
    }
}
