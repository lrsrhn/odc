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
package dk.ott.core.standardhandlers;

import dk.ott.core.event.EventHandler;
import dk.ott.core.eventhandling.AttributeValueCollector;
import dk.ott.core.eventhandling.ClearStoreHandler;
import dk.ott.core.eventhandling.TextValueCollector;

import java.util.ArrayList;
import java.util.List;

public class EventForwarderBuilder {
    private List<EventHandler> eventHandlers;

    private EventForwarderBuilder() {
        this.eventHandlers = new ArrayList<EventHandler>();
    }

    public static EventForwarderBuilder builder() {
        return new EventForwarderBuilder();
    }

    public EventForwarderBuilder attributeCollector(String attributeName, String storeKey) {
        eventHandlers.add(new AttributeValueCollector(attributeName, storeKey));
        return this;
    }

    public EventForwarderBuilder textValueCollector(String storeKey) {
        eventHandlers.add(new TextValueCollector(storeKey));
        return this;
    }

    public EventForwarderBuilder clearObjectStoreKeys(String... storeKey) {
        eventHandlers.add(new ClearStoreHandler(storeKey));
        return this;
    }

    public EventForwarder build() {
        return new EventForwarder(eventHandlers.toArray(new EventHandler[eventHandlers.size()]));
    }
}
