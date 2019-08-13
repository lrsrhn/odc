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

import javax.json.stream.JsonParser;

import static dk.ott.processing.TextTrimmer.trimToNull;

public class JsonObject extends BaseElementCursor {
    private JsonParser.Event currentEvent;
    private JsonParser jsonParser;

    public JsonObject(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public void setCurrentEvent(JsonParser.Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    @Override
    public String getText() {
        if (elementTextCache == null) {
            switch (currentEvent) {
                case VALUE_NULL:
                    break;
                case VALUE_TRUE:
                    elementTextCache = "true";
                    break;
                case VALUE_FALSE:
                    elementTextCache = "false";
                    break;
                case VALUE_NUMBER:
                case VALUE_STRING:
                    elementTextCache = trimToNull(jsonParser.getString());
                    break;
                default:
                    throw new RuntimeException("Unexpected event on while getText: " + currentEvent.name());
            }
        }
        return elementTextCache;
    }

    @Override
    public String getElementNS() {
        return "";
    }

    @Override
    public String getElementName() {
        return elementNameCache;
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
    public void clearCache() {
        elementTextCache = null;
    }

    public void setElementNameCache(String elementNameCache) {
        this.elementNameCache = elementNameCache;
    }
}
