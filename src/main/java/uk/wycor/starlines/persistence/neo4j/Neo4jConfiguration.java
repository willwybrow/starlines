package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.logging.Logger;

@Configuration
@EnableTransactionManagement
@EnableReactiveNeo4jRepositories(
        basePackages = "uk.wycor.starlines.persistence.neo4j",
        transactionManagerRef="neo4jTransactionManager"
)
public class Neo4jConfiguration {
    private static final Logger logger = Logger.getLogger(Neo4jConfiguration.class.getName());
    private final Driver driver;

    @Autowired
    public Neo4jConfiguration(Driver driver) {
        logger.info("Spinning up Neo4j Configuration");
        this.driver = driver;
    }

    @Bean
    public Neo4jTransactionManager neo4jTransactionManager() {
        return new Neo4jTransactionManager(driver);
    }

}
