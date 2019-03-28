package dk.ott.core.event;

import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.StructureElement;

public interface OnTextHandler {
    void onText(StructureElement structureElement, ObjectStore objectStore) throws Exception;
}
