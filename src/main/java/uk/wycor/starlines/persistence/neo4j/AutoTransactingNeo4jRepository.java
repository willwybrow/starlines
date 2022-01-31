package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.ogm.transaction.Transaction;
import uk.wycor.starlines.domain.ClusterID;
import uk.wycor.starlines.domain.GameRepository;
import uk.wycor.starlines.domain.Player;
import uk.wycor.starlines.domain.Star;
import uk.wycor.starlines.domain.StarControl;
import uk.wycor.starlines.domain.geometry.HexPoint;
import uk.wycor.starlines.persistence.NewPlayerWork;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AutoTransactingNeo4jRepository implements GameRepository {
    private final Neo4jGameRepository neo4jGameRepository;

    public AutoTransactingNeo4jRepository() {
        this.neo4jGameRepository = new Neo4jGameRepository();
    }

    @Override
    public Player setUpNewPlayer(NewPlayerWork newPlayerWork) {
        try (Transaction transaction = this.neo4jGameRepository.ogmSession.beginTransaction()) {
            var result = this.neo4jGameRepository.setUpNewPlayer(newPlayerWork);
            transaction.commit();
            return result;
        }
    }

    @Override
    public Set<StarControl> getClusterControllers(ClusterID clusterID) {
        try (Transaction transaction = this.neo4jGameRepository.ogmSession.beginTransaction()) {
            var result = this.neo4jGameRepository.getClusterControllers(clusterID);
            transaction.commit();
            return result;
        }
    }

    @Override
    public ClusterID populateNextStarfield(Map<HexPoint, Star> starfield) {
        try (Transaction transaction = this.neo4jGameRepository.ogmSession.beginTransaction()) {
            var result = this.neo4jGameRepository.populateNextStarfield(starfield);
            transaction.commit();
            return result;
        }
    }

    @Override
    public ClusterID pickUnoccupiedCluster() {
        try (Transaction transaction = this.neo4jGameRepository.ogmSession.beginTransaction()) {
            var result = this.neo4jGameRepository.pickUnoccupiedCluster();
            transaction.commit();
            return result;
        }
    }

    @Override
    public Collection<Star> getStarsInCluster(ClusterID clusterID) {
        try (Transaction transaction = this.neo4jGameRepository.ogmSession.beginTransaction()) {
            var result = this.neo4jGameRepository.getStarsInCluster(clusterID);
            transaction.commit();
            return result;
        }
    }

    @Override
    public Collection<Star> bestStarsInCluster(ClusterID clusterID) {
        try (Transaction transaction = this.neo4jGameRepository.ogmSession.beginTransaction()) {
            var result = this.neo4jGameRepository.bestStarsInCluster(clusterID);
            transaction.commit();
            return result;
        }
    }
}
