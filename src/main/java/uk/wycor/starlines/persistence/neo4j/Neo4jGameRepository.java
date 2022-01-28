package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;
import uk.wycor.starlines.domain.GameRepository;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.persistence.neo4j.entity.PlayerEntity;
import uk.wycor.starlines.persistence.neo4j.entity.StarEntity;

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
}
