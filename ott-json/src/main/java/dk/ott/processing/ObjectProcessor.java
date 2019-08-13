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
package dk.ott.processing;

import dk.ott.core.Node;
import dk.ott.processing.BaseElementProcessor;
import dk.ott.processing.EventAction;
import dk.ott.processing.JsonObject;
import dk.ott.processing.ObjectStore;
import dk.ott.processing.structures.JsonEventStack;
import dk.ott.processing.structures.StringStack;

import javax.json.stream.JsonParser;

class ObjectProcessor extends BaseElementProcessor<JsonParser, JsonObject> {

    private StringStack stringStack;
    private JsonEventStack jsonEventStack;

    public ObjectProcessor(Node nextNode, ObjectStore objectStore) {
        super(nextNode, objectStore);
        this.stringStack = new StringStack(10);
        this.jsonEventStack = new JsonEventStack(10);
    }

    public ObjectStore search(JsonParser jsonParser, JsonObject jsonObject) throws Exception {
        int currentDepth = 0;
        while (jsonParser.hasNext() && !jsonObject.mustStopProcessing()) {
            JsonParser.Event currentEvent = jsonParser.next();
            jsonObject.setCurrentEvent(currentEvent);
            switch (currentEvent) {
                case START_OBJECT:
                case START_ARRAY:
                    jsonObject.setElementNameCache("$");
                    jsonEventStack.push(currentEvent);
                    observableTreeTraverser.startElement(jsonObject, currentDepth++);
                    process(currentDepth, jsonObject, jsonParser);
            }
        }
        return objectStore;
    }

    private int process(int currentDepth, JsonObject jsonObject, JsonParser jsonParser) throws Exception {
        while (jsonParser.hasNext() && !jsonObject.mustStopProcessing()) {
            JsonParser.Event currentEvent = jsonParser.next();
            jsonObject.setCurrentEvent(currentEvent);
            switch (currentEvent) {
                case KEY_NAME:
                    stringStack.push(jsonObject.getElementName());
                    jsonEventStack.push(currentEvent);
                    jsonObject.setElementNameCache(jsonParser.getString());
                    if (observableTreeTraverser.startElement(jsonObject, currentDepth++) == EventAction.SKIP_ELEMENT) {
                        currentDepth = skipElement(currentDepth, jsonParser, jsonObject);
                    }
                    break;
                case START_ARRAY:
                    if (jsonEventStack.peek() != JsonParser.Event.KEY_NAME) {
                        stringStack.push(jsonObject.getElementName());
                        jsonObject.setElementNameCache("[]");
                        if (observableTreeTraverser.startElement(jsonObject, currentDepth++) == EventAction.SKIP_ELEMENT) {
                            currentDepth = skipElement(currentDepth, jsonParser, jsonObject);
                        }
                    }
                    jsonEventStack.push(currentEvent);
                    break;
                case START_OBJECT:
                   if (jsonEventStack.peek() != JsonParser.Event.KEY_NAME) {
                       stringStack.push(jsonObject.getElementName());
                        jsonObject.setElementNameCache("{}");
                        if (observableTreeTraverser.startElement(jsonObject, currentDepth++) == EventAction.SKIP_ELEMENT) {
                            currentDepth = skipElement(currentDepth, jsonParser, jsonObject);
                        }
                    }
                    jsonEventStack.push(currentEvent);
                    break;
                case VALUE_NULL:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NUMBER:
                case VALUE_STRING:
                    observableTreeTraverser.text(jsonObject);
                    jsonObject.clearCache();
                    if (jsonEventStack.peek() == JsonParser.Event.KEY_NAME) {
                        observableTreeTraverser.endElement(jsonObject, --currentDepth);
                        jsonEventStack.pop();
                        jsonObject.setElementNameCache(stringStack.pop());
                    }
                    break;
                case END_ARRAY:
                case END_OBJECT:
                    jsonEventStack.pop();
                    if (jsonEventStack.peek() == JsonParser.Event.KEY_NAME) {
                        jsonEventStack.pop();
                    }
                    observableTreeTraverser.endElement(jsonObject, --currentDepth);
                    if (currentDepth != 0) {
                        jsonObject.setElementNameCache(stringStack.pop());
                    }
            }
        }
        return currentDepth;
    }

    private int skipElement(int currentDepth, JsonParser jsonParser, JsonObject jsonObject) {
        int targetIndex = currentDepth;
        while (targetIndex <= currentDepth && jsonParser.hasNext()) {
            JsonParser.Event currentEvent = jsonParser.next();
            switch (currentEvent) {
                case KEY_NAME:
                    jsonEventStack.push(currentEvent);
                    currentDepth++;
                    break;
                case START_ARRAY:
                case START_OBJECT:
                    if (jsonEventStack.peek() != JsonParser.Event.KEY_NAME) {
                        currentDepth++;
                    }
                    jsonEventStack.push(currentEvent);
                    break;
                case END_OBJECT:
                case END_ARRAY:
                    jsonEventStack.pop();
                    if (jsonEventStack.peek() == JsonParser.Event.KEY_NAME) {
                        jsonEventStack.pop();
                    }
                    currentDepth--;
                    break;
                case VALUE_NULL:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NUMBER:
                case VALUE_STRING:
                    if (jsonEventStack.peek() == JsonParser.Event.KEY_NAME) {
                        currentDepth--;
                        jsonEventStack.pop();
                    }
                    break;
            }
        }
        jsonObject.setElementNameCache(stringStack.pop());
        return currentDepth;
    }
}
