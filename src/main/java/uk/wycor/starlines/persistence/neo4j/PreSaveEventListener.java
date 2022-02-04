package uk.wycor.starlines.persistence.neo4j;

import org.neo4j.ogm.session.event.Event;
import org.neo4j.ogm.session.event.EventListenerAdapter;
import uk.wycor.starlines.persistence.neo4j.entity.Entity;

import java.util.UUID;

class PreSaveEventListener extends EventListenerAdapter {
    @Override
    public void onPreSave(Event event) {
        Entity entity = (Entity) event.getObject();
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
    }
}
