package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.logging.Logger;

@Configuration
@EnableTransactionManagement
@EnableReactiveNeo4jRepositories(
        basePackages = "uk.wycor.starlines.persistence.neo4j",
        transactionManagerRef="reactiveTransactionManager"
)
public class Neo4jConfiguration {
    private static final Logger logger = Logger.getLogger(Neo4jConfiguration.class.getName());

    @Autowired
    public Neo4jConfiguration() {
        logger.info("Spinning up Neo4j Configuration");
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(Driver driver) {
        return new ReactiveNeo4jTransactionManager(driver);
    }

}
