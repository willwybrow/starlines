package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.config.ConfigurationSource;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class Neo4jSessionFactory {
    private final static ConfigurationSource props = new ClasspathConfigurationSource("local-neo4j.properties");
    private final static Configuration configuration = new Configuration
            .Builder(props)
            .useNativeTypes()
            .build();
    private final static SessionFactory sessionFactory = new SessionFactory(configuration, "uk.wycor.starlines.persistence.neo4j");
    private static final Neo4jSessionFactory factory = new Neo4jSessionFactory();

    public static Neo4jSessionFactory getInstance() {
        return factory;
    }

    private Neo4jSessionFactory() {
    }

    public Session getNeo4jSession() {
        return sessionFactory.openSession();
    }
}
