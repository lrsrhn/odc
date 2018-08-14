package dk.simpletools.odc.core.processing.stub;

import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.StructureElement;
import dk.simpletools.odc.core.processing.ValueStore;

public class ElementContext implements StructureElement {
    private ValueStore valueStore;
    private ObjectStore objectStore;
    private Element currentElement;

    public void setCurrentElement(Element currentElement) {
        this.currentElement = currentElement;
    }

    @Override
    public String getElementNS() {
        return null;
    }

    @Override
    public String getElementName() {
        return currentElement.getElementName();
    }

    @Override
    public String getElementValue() {
        return currentElement.getTextValue();
    }

    @Override
    public String getAttributeValue(String attributeName) {
        return null;
    }

    @Override
    public boolean hasAttribute(String attributeName) {
        return false;
    }

    @Override
    public ValueStore getValueStore() {
        return valueStore;
    }

    @Override
    public ObjectStore getObjectStore() {
        return objectStore;
    }

    @Override
    public String[] getValueArray() {
        return new String[0];
    }

    @Override
    public String getRawElementValue() {
        return currentElement.getTextValue();
    }

    @Override
    public void clearCache() {

    }
}
