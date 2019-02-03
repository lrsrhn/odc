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

import dk.simpletools.odc.core.finder.ElementFinder;
import dk.simpletools.odc.core.processing.BaseElementProcessor;
import dk.simpletools.odc.core.processing.JsonObject;
import dk.simpletools.odc.core.processing.ObjectStore;

import javax.json.stream.JsonParser;

import static javax.json.stream.JsonParser.Event.END_ARRAY;

class ObjectProcessor extends BaseElementProcessor<JsonParser, JsonObject>{

    public ObjectProcessor(ElementFinder nextElementFinder, JsonObject jsonObject) {
        super(nextElementFinder, jsonObject);
    }

    public ObjectStore search(JsonParser jsonParser, JsonObject jsonObject) throws Exception {
        int currentDepth = -1;
        while (jsonParser.hasNext()) {
            JsonParser.Event currentEvent = jsonParser.next();
            jsonObject.setCurrentEvent(currentEvent);
            switch (currentEvent) {
                case START_OBJECT:
                    currentDepth = processObject("$", currentDepth, jsonObject, jsonParser);
                    continue;
                case START_ARRAY:
                    currentDepth = processArray("$", currentDepth, jsonObject, jsonParser);
            }
        }
        return jsonObject.getObjectStore();
    }

    private int processArray(String currentKey, int currentDepth, JsonObject jsonObject, JsonParser jsonParser) throws Exception {
        currentDepth++;
        jsonObject.setKeyName(currentKey);
        observablePathTraverser.startElement(jsonObject, currentDepth);
        if (jsonObject.getCurrentEvent() == END_ARRAY) {
            jsonObject.setKeyName(currentKey);
            observablePathTraverser.endElement(jsonObject, currentDepth);
            return --currentDepth;
        }
        while (jsonParser.hasNext()) {
            JsonParser.Event currentEvent = jsonParser.next();
            jsonObject.setCurrentEvent(currentEvent);
            switch (currentEvent) {
                case START_ARRAY:
                    processArray("[]", currentDepth, jsonObject, jsonParser);
                    break;
                case START_OBJECT:
                    currentDepth = processObject("{}", currentDepth, jsonObject, jsonParser);
                    break;
                case END_ARRAY:
                    jsonObject.setKeyName(currentKey);
                    observablePathTraverser.endElement(jsonObject, currentDepth);
                    return --currentDepth;
            }
        }
        return --currentDepth;
    }

    private int processObject(String currentKey, int currentDepth, JsonObject jsonObject, JsonParser jsonParser) throws Exception {
        currentDepth++;
        jsonObject.setKeyName(currentKey);
        observablePathTraverser.startElement(jsonObject, currentDepth);
        String nextKey = "noKey";
        while (jsonParser.hasNext()) {
            JsonParser.Event currentEvent = jsonParser.next();
            jsonObject.setCurrentEvent(currentEvent);
            switch (currentEvent) {
                case KEY_NAME:
                    nextKey = jsonParser.getString();
                    jsonObject.setKeyName(nextKey);
                    break;
                case START_ARRAY:
                    currentDepth = processArray(nextKey, currentDepth, jsonObject, jsonParser);
                    break;
                case START_OBJECT:
                    currentDepth = processObject(nextKey, currentDepth, jsonObject, jsonParser);
                    break;
                case VALUE_NULL:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NUMBER:
                case VALUE_STRING:
                    currentDepth++;
                    observablePathTraverser.startElement(jsonObject, currentDepth);
                    observablePathTraverser.endElement(jsonObject, currentDepth);
                    currentDepth--;
                    break;
                case END_OBJECT:
                    jsonObject.setKeyName(currentKey);
                    observablePathTraverser.endElement(jsonObject, currentDepth);
                    return --currentDepth;
            }
        }
        return --currentDepth;
    }
}
