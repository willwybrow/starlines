package uk.wycor.starlines.persistence.neo4j;


import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Transactional(transactionManager="reactiveTransactionManager")
public @interface Neo4jTransactional {
}