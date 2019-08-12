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
import dk.ott.core.event.OnStartHandler;
import dk.ott.core.event.OnTextHandler;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.ElementCursor;
// TODO: Remove EventHandler and use EventHandler instead
public class EventForwarder implements OnStartHandler, OnTextHandler {
    private EventHandler[] eventHandlers;

    public EventForwarder(EventHandler[] eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    @Override
    public void onStart(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.onStart(elementCursor, objectStore);
        }
    }

    @Override
    public void onText(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
        for (EventHandler eventHandler : eventHandlers) {
            eventHandler.onText(elementCursor, objectStore);
        }
    }
}
