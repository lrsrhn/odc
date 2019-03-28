package dk.ott.core.eventhandling;

import dk.ott.core.event.EventHandler;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.StructureElement;

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

    public void handle(StructureElement structureElement, ObjectStore objectStore) {
        for (String storeKey : keysToClear) {
            objectStore.clearKey(storeKey);
        }
    }

    @Override
    public void onEnd(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        handle(structureElement, objectStore);
    }

    @Override
    public void onStart(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        handle(structureElement, objectStore);
    }

    @Override
    public void onText(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        handle(structureElement, objectStore);
    }
}