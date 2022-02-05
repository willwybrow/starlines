package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.ogm.session.event.Event;
import org.neo4j.ogm.session.event.EventListenerAdapter;
import uk.wycor.starlines.persistence.neo4j.entity.Entity;
import uk.wycor.starlines.persistence.neo4j.entity.Relationship;

import java.util.UUID;
import java.util.logging.Logger;

class PreSaveEventListener extends EventListenerAdapter {
    private static final Logger logger = Logger.getLogger(PreSaveEventListener.class.getName());

    @Override
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
    }
}
