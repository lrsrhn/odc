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
package dk.ott.core;

import dk.ott.event.OnEndHandler;
import dk.ott.event.OnStartHandler;
import dk.ott.predicate.Predicate;

public final class BinEdge {
    private OnStartHandler onStartHandler;
    private TextLocation textLocation;
    private OnEndHandler onEndHandler;
    private Predicate filter;

    public BinEdge() {
    }

    public BinEdge(OnStartHandler onStartHandler, OnEndHandler onEndHandler) {
        this.onStartHandler = onStartHandler;
        this.onEndHandler = onEndHandler;
    }

    public OnStartHandler getOnStartHandler() {
        return onStartHandler;
    }

    public BinEdge setOnStartHandler(OnStartHandler onStartHandler) {
        this.onStartHandler = onStartHandler;
        return this;
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

    public BinEdge setTextLocation(TextLocation textLocation) {
        this.textLocation = textLocation;
        return this;
    }

    public OnEndHandler getOnEndHandler() {
        return onEndHandler;
    }

    public BinEdge setOnEndHandler(OnEndHandler onEndHandler) {
        this.onEndHandler = onEndHandler;
        return this;
    }

    public Predicate getFilter() {
        return filter;
    }

    public BinEdge setFilter(Predicate filter) {
        this.filter = filter;
        return this;
    }

    public BinEdge merge(BinEdge mergee) {
        if (onStartHandler == null) {
            onStartHandler = mergee.onStartHandler;
        } else if (mergee.onStartHandler != null && !onStartHandler.equals(mergee.onStartHandler)) {
            throw new IllegalStateException("Edge cannot point to different on start handlers when merging");
        }
        if (textLocation == null) {
            textLocation = mergee.textLocation;
        } else if (mergee.textLocation != null && !textLocation.equals(mergee.textLocation)) {
            throw new IllegalStateException("Edge cannot point to different text locations when merging");
        }
        if (onEndHandler == null) {
            onEndHandler = mergee.onEndHandler;
        } else if (mergee.onEndHandler != null && !onEndHandler.equals(mergee.onEndHandler)) {
            throw new IllegalStateException("Edge cannot point to different on end handlers when merging");
        }
        if (filter == null) {
            filter = mergee.filter;
        } else if (mergee.filter != null && !filter.equals(mergee.filter)) {
            throw new IllegalStateException("Edge cannot point to different filters when merging");
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinEdge edge = (BinEdge) o;

        if (onStartHandler != null ? !onStartHandler.equals(edge.onStartHandler) : edge.onStartHandler != null)
            return false;
        if (textLocation != null ? !textLocation.equals(edge.textLocation) : edge.textLocation != null) return false;
        if (onEndHandler != null ? !onEndHandler.equals(edge.onEndHandler) : edge.onEndHandler != null) return false;
        return filter != null ? filter.equals(edge.filter) : edge.filter == null;
    }

    @Override
    public int hashCode() {
        int result = onStartHandler != null ? onStartHandler.hashCode() : 0;
        result = 31 * result + (textLocation != null ? textLocation.hashCode() : 0);
        result = 31 * result + (onEndHandler != null ? onEndHandler.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }
}
