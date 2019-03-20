package dk.simpletools.odc.core.finder;

import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.StructureElement;

public interface OnTextHandler {
    void onText(StructureElement structureElement, ObjectStore objectStore) throws Exception;
}
