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
package dk.ott.core.finder;

import dk.ott.core.predicate.Predicate;

public final class SearchLocation {
    private ElementFinder elementFinder;
    private OnStartHandler onStartHandler;
    private TextLocation textLocation;
    private OnEndHandler onEndHandler;
    private Predicate filter;

    public SearchLocation() {
    }

    public SearchLocation(ElementFinder elementFinder, OnStartHandler onStartHandler, OnEndHandler onEndHandler) {
        this.elementFinder = elementFinder;
        this.onStartHandler = onStartHandler;
        this.onEndHandler = onEndHandler;
    }

    public ElementFinder getElementFinder() {
        return elementFinder;
    }

    public void setElementFinder(ElementFinder elementFinder) {
        this.elementFinder = elementFinder;
    }

    public OnStartHandler getOnStartHandler() {
        return onStartHandler;
    }

    public void setOnStartHandler(OnStartHandler onStartHandler) {
        this.onStartHandler = onStartHandler;
    }

    public OnEndHandler getOnEndHandler() {
        return onEndHandler;
    }

    public void setOnEndHandler(OnEndHandler onEndHandler) {
        this.onEndHandler = onEndHandler;
    }

    public void setFilter(Predicate filter) {
        this.filter = filter;
    }

    public Predicate getFilter() {
        return filter;
    }

    public TextLocation getTextLocation() {
        return textLocation;
    }

    public TextLocation getOrCreateTextLocation() {
        if (textLocation == null) {
            this.textLocation = new TextLocation();
        }
        return textLocation;
    }

    public void setTextLocation(TextLocation textLocation) {
        this.textLocation = textLocation;
    }
}
