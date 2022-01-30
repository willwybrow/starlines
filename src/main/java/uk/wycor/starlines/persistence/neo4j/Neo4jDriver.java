package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.ConfigurationSource;

public class Neo4jDriver {
    private final static ConfigurationSource CONFIGURATION_SOURCE = new ClasspathConfigurationSource("local-neo4j.properties");

    final static Driver DRIVER = GraphDatabase
            .driver(CONFIGURATION_SOURCE.properties().getProperty("URI"), AuthTokens.basic(CONFIGURATION_SOURCE.properties().getProperty("username"), CONFIGURATION_SOURCE.properties().getProperty("password")));
}
