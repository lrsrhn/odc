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

import dk.ott.core.dsl.TreeEdgeReference;
import dk.ott.core.event.OnEndHandler;
import dk.ott.core.event.OnStartHandler;
import dk.ott.core.predicate.Predicate;

public final class SearchLocation {
    private boolean isRelative;
    private ElementFinder elementFinder;
    private OnStartHandler onStartHandler;
    private TextLocation textLocation;
    private OnEndHandler onEndHandler;
    private Predicate filter;

    public SearchLocation(boolean isRelative) {
        this.isRelative = isRelative;
    }

    public SearchLocation(ElementFinder elementFinder, OnStartHandler onStartHandler, OnEndHandler onEndHandler, boolean isRelative) {
        this.elementFinder = elementFinder;
        this.onStartHandler = onStartHandler;
        this.onEndHandler = onEndHandler;
        this.isRelative = isRelative;
    }

    public ElementFinder getElementFinder() {
        return elementFinder;
    }

    public SearchLocation setElementFinder(ElementFinder elementFinder) {
        this.elementFinder = elementFinder;
        return this;
    }

    public OnStartHandler getOnStartHandler() {
        return onStartHandler;
    }

    public SearchLocation setOnStartHandler(OnStartHandler onStartHandler) {
        this.onStartHandler = onStartHandler;
        return this;
    }

    public OnEndHandler getOnEndHandler() {
        return onEndHandler;
    }

    public SearchLocation setOnEndHandler(OnEndHandler onEndHandler) {
        this.onEndHandler = onEndHandler;
        return this;
    }

    public SearchLocation setFilter(Predicate filter) {
        this.filter = filter;
        return this;
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

    public boolean isRelative() {
        return isRelative;
    }

    public SearchLocation setRelative(boolean relative) {
        isRelative = relative;
        return this;
    }

    public SearchLocation setTextLocation(TextLocation textLocation) {
        this.textLocation = textLocation;
        return this;
    }

    public TreeEdgeReference toTreeEdgeReference(ElementFinder elementFinder) {
        return new TreeEdgeReference(elementFinder, this);
    }

    public SearchLocation merge(SearchLocation mergee) {
        if (elementFinder == null) {
            elementFinder = mergee.elementFinder;
        } else if (mergee.elementFinder != null && elementFinder != mergee.elementFinder) {
            elementFinder.mergeElementFinder(mergee.elementFinder);
        }
        if (onStartHandler == null) {
            onStartHandler = mergee.onStartHandler;
        } else if (mergee.onStartHandler != null && !onStartHandler.equals(mergee.onStartHandler)) {
            throw new IllegalStateException("SearchLocation cannot point to different on start handlers when merging");
        }
        if (textLocation == null) {
            textLocation = mergee.textLocation;
        } else if (mergee.textLocation != null && !textLocation.equals(mergee.textLocation)) {
            throw new IllegalStateException("SearchLocation cannot point to different text locations when merging");
        }
        if (onEndHandler == null) {
            onEndHandler = mergee.onEndHandler;
        } else if (mergee.onEndHandler != null && !onEndHandler.equals(mergee.onEndHandler)) {
            throw new IllegalStateException("SearchLocation cannot point to different on end handlers when merging");
        }
        if (filter == null) {
            filter = mergee.filter;
        } else if (mergee.filter != null && !filter.equals(mergee.filter)) {
            throw new IllegalStateException("SearchLocation cannot point to different filters when merging");
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchLocation that = (SearchLocation) o;

        if (isRelative != that.isRelative) return false;
        if (elementFinder != null ? !elementFinder.equals(that.elementFinder) : that.elementFinder != null)
            return false;
        if (onStartHandler != null ? !onStartHandler.equals(that.onStartHandler) : that.onStartHandler != null)
            return false;
        if (textLocation != null ? !textLocation.equals(that.textLocation) : that.textLocation != null) return false;
        if (onEndHandler != null ? !onEndHandler.equals(that.onEndHandler) : that.onEndHandler != null) return false;
        return filter != null ? filter.equals(that.filter) : that.filter == null;
    }

    @Override
    public int hashCode() {
        int result = (isRelative ? 1 : 0);
        result = 31 * result + (elementFinder != null ? elementFinder.hashCode() : 0);
        result = 31 * result + (onStartHandler != null ? onStartHandler.hashCode() : 0);
        result = 31 * result + (textLocation != null ? textLocation.hashCode() : 0);
        result = 31 * result + (onEndHandler != null ? onEndHandler.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }
}
