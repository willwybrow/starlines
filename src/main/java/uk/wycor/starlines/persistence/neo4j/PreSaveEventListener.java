package uk.wycor.starlines.persistence.neo4j;

import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
class PreSaveEventListener {
    private static final Logger logger = Logger.getLogger(PreSaveEventListener.class.getName());
/*

    public void onPreSave(Event event) {
        try {
            Entity entity = (Entity) event.getObject();
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            return;
        } catch (ClassCastException exception) {
            logger.warning("Failed to cast saved object to Entity class");
        }
        try {
            Relationship relationship = (Relationship) event.getObject();
            if (relationship.getId() == null) {
                relationship.setId(UUID.randomUUID());
            }
        } catch (ClassCastException exception) {
            logger.warning("Failed to cast saved object to Relationship class");
        }
    }*/
}
