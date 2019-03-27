package dk.ott.core.eventhandling;

import dk.ott.core.finder.ElementHandler;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.StructureElement;

public interface EventHandler extends ElementHandler {
    void handle(StructureElement structureElement, ObjectStore objectStore);
}
