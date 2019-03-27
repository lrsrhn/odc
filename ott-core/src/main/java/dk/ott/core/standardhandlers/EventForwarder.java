package dk.ott.core.standardhandlers;

import dk.ott.core.eventhandling.EventHandler;
import dk.ott.core.finder.OnStartHandler;
import dk.ott.core.finder.OnTextHandler;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.StructureElement;

public class EventForwarder implements OnStartHandler, OnTextHandler {
    private EventHandler[] eventHandlers;

    public EventForwarder(EventHandler[] eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    @Override
    public void startElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.handle(structureElement, objectStore);
        }
    }

    @Override
    public void onText(StructureElement structureElement, ObjectStore objectStore) {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.handle(structureElement, objectStore);
        }
    }
}
