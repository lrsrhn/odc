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

import dk.ott.dsl.EdgeReference;
import dk.ott.event.OnEndHandler;
import dk.ott.event.OnStartHandler;
import dk.ott.predicate.Predicate;

public final class Edge {
    private boolean isRelative;
    private Node childNode;
    private OnStartHandler onStartHandler;
    private TextLocation textLocation;
    private OnEndHandler onEndHandler;
    private Predicate filter;

    public Edge(boolean isRelative) {
        this.isRelative = isRelative;
    }

    public Edge(Node childNode, OnStartHandler onStartHandler, OnEndHandler onEndHandler, boolean isRelative) {
        this.childNode = childNode;
        this.onStartHandler = onStartHandler;
        this.onEndHandler = onEndHandler;
        this.isRelative = isRelative;
    }

    public Node getChildNode() {
        return childNode;
    }

    public Edge setChildNode(Node childNode) {
        this.childNode = childNode;
        return this;
    }

    public OnStartHandler getOnStartHandler() {
        return onStartHandler;
    }

    public Edge setOnStartHandler(OnStartHandler onStartHandler) {
        this.onStartHandler = onStartHandler;
        return this;
    }

    public OnEndHandler getOnEndHandler() {
        return onEndHandler;
    }

    public Edge setOnEndHandler(OnEndHandler onEndHandler) {
        this.onEndHandler = onEndHandler;
        return this;
    }

    public Edge setFilter(Predicate filter) {
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

    public Edge setRelative(boolean relative) {
        isRelative = relative;
        return this;
    }

    public Edge setTextLocation(TextLocation textLocation) {
        this.textLocation = textLocation;
        return this;
    }

    public EdgeReference toTreeEdgeReference(Node node) {
        return new EdgeReference(node, this);
    }

    public Edge merge(Edge mergee) {
        if (childNode == null) {
            childNode = mergee.childNode;
        } else if (mergee.childNode != null && childNode != mergee.childNode) {
            childNode.mergeNode(mergee.childNode);
        }
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

        Edge that = (Edge) o;

        if (isRelative != that.isRelative) return false;
        if (childNode != null ? !childNode.equals(that.childNode) : that.childNode != null)
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
        result = 31 * result + (childNode != null ? childNode.hashCode() : 0);
        result = 31 * result + (onStartHandler != null ? onStartHandler.hashCode() : 0);
        result = 31 * result + (textLocation != null ? textLocation.hashCode() : 0);
        result = 31 * result + (onEndHandler != null ? onEndHandler.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }
}
