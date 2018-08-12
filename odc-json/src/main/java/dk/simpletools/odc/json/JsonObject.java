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
package dk.simpletools.odc.json;

import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.StructureElement;
import dk.simpletools.odc.core.processing.ValueStore;

import javax.json.stream.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class JsonObject implements StructureElement {
    private JsonParser.Event currentEvent;
    private JsonParser jsonParser;
    private String keyName;
    private ValueStore valueStore;
    private ObjectStore objectStore;
    private String elementValueCache;
    private String[] elementValuesCache;

    public JsonObject(JsonParser jsonParser) {
        this.valueStore = new ValueStore();
        this.jsonParser = jsonParser;
        this.objectStore = new ObjectStore();
    }

    public void setCurrentEvent(JsonParser.Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public JsonParser.Event getCurrentEvent() {
        return currentEvent;
    }

    @Override
    public String getElementValue() {
        if (elementValueCache == null) {
            switch (currentEvent) {
                case VALUE_NULL:
                    elementValueCache = null;
                    break;
                case VALUE_TRUE:
                    elementValueCache = "true";
                    break;
                case VALUE_FALSE:
                    elementValueCache = "false";
                    break;
                case VALUE_NUMBER:
                    elementValueCache = jsonParser.getString();
                    break;
                case VALUE_STRING:
                    elementValueCache = jsonParser.getString();
                    break;
                default:
                    throw new RuntimeException("Unexpected event on while getElementValue: " + currentEvent.name());
            }
        }
        return elementValueCache;
    }

    @Override
    public String getElementNS() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getElementName() {
        return keyName;
    }

    @Override
    public String getAttributeValue(String attributeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasAttribute(String attributeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ValueStore getValueStore() {
        return valueStore;
    }

    @Override
    public ObjectStore getObjectStore() {
        return objectStore;
    }

    public String[] getValueArray() {
        if (elementValuesCache != null) {
            return elementValuesCache;
        }
        List<String> array = new ArrayList<String>();
        while(jsonParser.hasNext()) {
            currentEvent = jsonParser.next();
            switch (currentEvent) {
                case END_ARRAY:
                    elementValuesCache = array.toArray(new String[array.size()]);
                    return elementValuesCache;
                default:
                    elementValueCache = null;
                    array.add(getElementValue());
                    break;
            }
        }
        elementValueCache = null;
        elementValuesCache = array.toArray(new String[array.size()]);
        return elementValuesCache;
    }

    @Override
    public String getRawElementValue() {
        return getElementValue();
    }

    @Override
    public void clearCache() {
        elementValuesCache = null;
        elementValueCache = null;
    }

    void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
