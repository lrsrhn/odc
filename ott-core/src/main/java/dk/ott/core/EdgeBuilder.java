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
import dk.ott.event.OnTextHandler;
import dk.ott.predicate.Predicate;

public class EdgeBuilder {
    private Edge edge;
    private Node parentNode;

    public EdgeBuilder(Node parentNode, Edge edge) {
        if (edge == null) {
            throw new RuntimeException("Passed edge is null!");
        }
        this.edge = edge;
        this.parentNode = parentNode;
    }

    public EdgeBuilder filter(Predicate filter) {
        this.edge.setFilter(filter);
        return this;
    }

    public EdgeBuilder onStartHandler(OnStartHandler onStartHandler) {
        this.edge.setOnStartHandler(onStartHandler);
        return this;
    }

    public EdgeBuilder onEndHandler(OnEndHandler onEndHandler) {
        this.edge.setOnEndHandler(onEndHandler);
        return this;
    }

    public EdgeBuilder textAsRaw() {
        edge.getOrCreateTextLocation().setRaw(true);
        return this;
    }

    public EdgeBuilder onTextHandler(OnTextHandler onTextHandler) {
        this.edge.getOrCreateTextLocation().setOnTextHandler(onTextHandler);
        return this;
    }

    public EdgeBuilder textFilter(Predicate textFilter) {
        this.edge.getTextLocation().setTextFilter(textFilter);
        return this;
    }

    public EdgeBuilder addSearchElementFinder(String elementName, boolean isRelative) {
        return addSearchElementFinder().buildEdge(elementName, isRelative);
    }

    public Node addSearchElementFinder() {
        Node node = edge.getChildNode();
        if (node == null) {
            node = new NodeElementEdge().getReference();
            this.edge.setChildNode(node);
        }
        return node;
    }

    public EdgeBuilder addPredicateElementFinder(Predicate predicate) {
        return addPredicateElementFinder().buildEdge(predicate);
    }

    public Node addPredicateElementFinder() {
        Node node = edge.getChildNode();
        if (node == null) {
            node = new NodePredicateEdge().getReference();
            this.edge.setChildNode(node);
        }
        return node;
    }

    public EdgeBuilder addAllElementFinder() {
        Node node = edge.getChildNode();
        if (node == null) {
            node = new NodeStar();
            this.edge.setChildNode(node);
        }
        return node.buildEdge(null);
    }

    public EdgeReference toTreeEdgeReference() {
        return new EdgeReference(parentNode, edge);
    }

    public Edge build() {
        return edge;
    }
}
