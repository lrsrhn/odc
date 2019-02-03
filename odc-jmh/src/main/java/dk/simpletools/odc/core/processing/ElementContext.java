/**
 * The MIT License
 * Copyright © 2018 Lars Storm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dk.simpletools.odc.core.processing;

import dk.simpletools.odc.core.stub.Element;

public class ElementContext implements InternalStructureElement {
    private ValueStore valueStore;
    private ObjectStore objectStore;
    private Element currentElement;

    public ElementContext(ObjectStore objectStore) {
        this.objectStore = objectStore == null ? new ObjectStore() : objectStore;
        this.valueStore = new ValueStore();
    }

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

    @Override
    public void skipElement() {
        // TODO: implement
    }
}
