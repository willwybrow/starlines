package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;
import org.neo4j.ogm.types.spatial.CartesianPoint2d;
import uk.wycor.starlines.domain.GameRepository;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.Point;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.persistence.neo4j.entity.PlayerEntity;
import uk.wycor.starlines.persistence.neo4j.entity.StarEntity;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

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
            session.save(PlayerEntity.fromPlayer(player));
            // TODO tomorrow: pick a cluster and have their first ship(s) orbit the best star of it

            transaction.commit();
        }
        return player;
    }

    public int populateNextStarfield(Map<Point, Star> starfield) {
        try (Transaction transaction = session.beginTransaction(Transaction.Type.READ_WRITE)) {
            int nextClusterID = latestGeneratedCluster() + 1;
            starfield.forEach((point, star) -> {
                session.save(new StarEntity(nextClusterID, new CartesianPoint2d(point.x(), point.y()), star.getName(), star.getCurrentMass(), star.getMaximumMass(), Collections.emptySet()));
            });
            transaction.commit();
            return nextClusterID;
        }
    }

    private int latestGeneratedCluster() {
        try {
            return session.query(Integer.class, "MATCH (star:Star) RETURN max(star.clusterID) AS latestClusterID", Collections.emptyMap()).iterator().next();
        } catch (NullPointerException | NoSuchElementException e) {
            return 0; // beginning of game
        }
    }
}
