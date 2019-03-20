package dk.simpletools.odc.core.standardhandlers;

import dk.simpletools.odc.core.eventhandling.EventHandler;
import dk.simpletools.odc.core.finder.OnStartHandler;
import dk.simpletools.odc.core.finder.OnTextHandler;
import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.StructureElement;

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
