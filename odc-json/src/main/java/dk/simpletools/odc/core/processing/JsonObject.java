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

import javax.json.stream.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class JsonObject implements InternalStructureElement {
    private JsonParser.Event currentEvent;
    private JsonParser jsonParser;
    private String keyName;
    private ValueStore valueStore;
    private ObjectStore objectStore;
    private String elementValueCache;

    public JsonObject(JsonParser jsonParser, ObjectStore objectStore) {
        this.objectStore = objectStore == null ? new ObjectStore() : objectStore;
        this.jsonParser = jsonParser;
        this.valueStore = new ValueStore();
    }

    public void setCurrentEvent(JsonParser.Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public JsonParser.Event getCurrentEvent() {
        return currentEvent;
    }

    @Override
    public String getText() {
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
                    throw new RuntimeException("Unexpected event on while getText: " + currentEvent.name());
            }
        }
        return elementValueCache;
    }

    @Override
    public String getElementNS() {
        return "";
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

//    public String[] getValueArray() {
//        if (elementValuesCache != null) {
//            return elementValuesCache;
//        }
//        List<String> array = new ArrayList<String>();
//        while(jsonParser.hasNext()) {
//            currentEvent = jsonParser.next();
//            switch (currentEvent) {
//                case END_ARRAY:
//                    elementValuesCache = array.toArray(new String[array.size()]);
//                    return elementValuesCache;
//                default:
//                    elementValueCache = null;
//                    array.add(getText());
//                    break;
//            }
//        }
//        elementValueCache = null;
//        elementValuesCache = array.toArray(new String[array.size()]);
//        return elementValuesCache;
//    }

    @Override
    public String getRawElementValue() {
        return getText();
    }

    @Override
    public void clearCache() {
        elementValueCache = null;
    }

    @Override
    public void skipElement() {
        // TODO
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
