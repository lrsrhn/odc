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
package dk.ott.json;

import dk.ott.core.finder.ElementFinder;
import dk.ott.core.processing.BaseElementProcessor;
import dk.ott.core.processing.JsonObject;
import dk.ott.core.processing.ObjectStore;

import javax.json.stream.JsonParser;

class ObjectProcessor extends BaseElementProcessor<JsonParser, JsonObject> {

    private StringStack stringStack;
    private JsonEventStack jsonEventStack;

    public ObjectProcessor(ElementFinder nextElementFinder, JsonObject jsonObject) {
        super(nextElementFinder, jsonObject);
        this.stringStack = new StringStack(10);
        this.jsonEventStack = new JsonEventStack(10);
    }

    public ObjectStore search(JsonParser jsonParser, JsonObject jsonObject) throws Exception {
        int currentDepth = 0;
        while (jsonParser.hasNext()) {
            JsonParser.Event currentEvent = jsonParser.next();
            jsonObject.setCurrentEvent(currentEvent);
            switch (currentEvent) {
                case START_OBJECT:
                case START_ARRAY:
                    jsonObject.setKeyName("$");
                    jsonEventStack.push(currentEvent);
                    observablePathTraverser.startElement(jsonObject, currentDepth++);
                    process(currentDepth, jsonObject, jsonParser);
            }
        }
        return objectStore;
    }

    private int process(int currentDepth, JsonObject jsonObject, JsonParser jsonParser) throws Exception {
        while (jsonParser.hasNext()) {
            JsonParser.Event currentEvent = jsonParser.next();
            jsonObject.setCurrentEvent(currentEvent);
            switch (currentEvent) {
                case KEY_NAME:
                    stringStack.push(jsonObject.getElementName());
                    jsonEventStack.push(currentEvent);
                    jsonObject.setKeyName(jsonParser.getString());
                    observablePathTraverser.startElement(jsonObject, currentDepth++);
                    break;
                case START_ARRAY:
                    if (jsonEventStack.peek() != JsonParser.Event.KEY_NAME) {
                        stringStack.push(jsonObject.getElementName());
                        jsonObject.setKeyName("[]");
                        observablePathTraverser.startElement(jsonObject, currentDepth++);
                    }
                    jsonEventStack.push(currentEvent);
                    break;
                case START_OBJECT:
                   if (jsonEventStack.peek() != JsonParser.Event.KEY_NAME) {
                       stringStack.push(jsonObject.getElementName());
                        jsonObject.setKeyName("{}");
                        observablePathTraverser.startElement(jsonObject, currentDepth++);
                    }
                    jsonEventStack.push(currentEvent);
                    break;
                case VALUE_NULL:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NUMBER:
                case VALUE_STRING:
                    observablePathTraverser.text(jsonObject);
                    jsonObject.clearCache();
                    if (jsonEventStack.peek() == JsonParser.Event.KEY_NAME) {
                        observablePathTraverser.endElement(jsonObject, --currentDepth);
                        jsonEventStack.pop();
                        jsonObject.setKeyName(stringStack.pop());
                    }
                    break;
                case END_ARRAY:
                case END_OBJECT:
                    jsonEventStack.pop();
                    if (jsonEventStack.size() > 0 && jsonEventStack.peek() == JsonParser.Event.KEY_NAME) {
                        jsonEventStack.pop();
                    }
                    observablePathTraverser.endElement(jsonObject, --currentDepth);
                    if (currentDepth != 0) {
                        jsonObject.setKeyName(stringStack.pop());
                    }
            }
        }
        return currentDepth;
    }
}
