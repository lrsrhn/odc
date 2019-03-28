package dk.ott.core.standardhandlers;

import dk.ott.core.event.EventHandler;
import dk.ott.core.event.OnStartHandler;
import dk.ott.core.event.OnTextHandler;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.StructureElement;
// TODO: Remove EventHandler and use EventHandler instead
public class EventForwarder implements OnStartHandler, OnTextHandler {
    private EventHandler[] eventHandlers;

    public EventForwarder(EventHandler[] eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    @Override
    public void onStart(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.onStart(structureElement, objectStore);
        }
    }

    @Override
    public void onText(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.onText(structureElement, objectStore);
        }
    }
}
