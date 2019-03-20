package dk.simpletools.odc.core.eventhandling;

import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.StructureElement;

/**
 * Clear specific keys in the object store
 */
public class ClearStoreHandler implements EventHandler {
    private String[] keysToClear;

    public ClearStoreHandler(String[] keysToClear) {
        this.keysToClear = keysToClear;
    }

    public static ClearStoreHandler clearKeys(String... keysToClear) {
        return new ClearStoreHandler(keysToClear);
    }

    @Override
    public void handle(StructureElement structureElement, ObjectStore objectStore) {
        for (String storeKey : keysToClear) {
            objectStore.clearKey(storeKey);
        }
    }

    @Override
    public void endElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        handle(structureElement, objectStore);
    }

    @Override
    public void startElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        handle(structureElement, objectStore);
    }

    @Override
    public void onText(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        handle(structureElement, objectStore);
    }
}