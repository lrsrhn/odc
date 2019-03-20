package dk.simpletools.odc.core.eventhandling;

import dk.simpletools.odc.core.finder.ElementHandler;
import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.StructureElement;

public interface EventHandler extends ElementHandler {
    void handle(StructureElement structureElement, ObjectStore objectStore);
}
