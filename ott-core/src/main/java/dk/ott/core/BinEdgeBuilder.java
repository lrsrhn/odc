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

import dk.ott.bintree.BinTree;
import dk.ott.bintree.PositionalIndex;
import dk.ott.event.OnEndHandler;
import dk.ott.event.OnStartHandler;
import dk.ott.event.OnTextHandler;
import dk.ott.predicate.Predicate;

import static dk.ott.bintree.NodeBuilder.nodeBuilder;

public class BinEdgeBuilder {
    private BinEdge edge;
    private PositionalIndex positionalIndex;
    private BinTree binTree;

    public BinEdgeBuilder(PositionalIndex positionalIndex, BinTree binTree, BinEdge edge) {
        if (edge == null) {
            throw new RuntimeException("Passed edge is null!");
        }
        this.edge = edge;
        this.positionalIndex = positionalIndex;
        this.binTree = binTree;
    }

    public BinEdgeBuilder filter(Predicate filter) {
        this.edge.setFilter(filter);
        return this;
    }

    public BinEdgeBuilder onStartHandler(OnStartHandler onStartHandler) {
        this.edge.setOnStartHandler(onStartHandler);
        return this;
    }

    public BinEdgeBuilder onEndHandler(OnEndHandler onEndHandler) {
        this.edge.setOnEndHandler(onEndHandler);
        return this;
    }

    public BinEdgeBuilder textAsRaw() {
        edge.getOrCreateTextLocation().setRaw(true);
        positionalIndex.setNode(nodeBuilder(positionalIndex.getNode()).hasTextHandler(true).build());
        binTree.setNode(positionalIndex);
        return this;
    }

    public BinEdgeBuilder onTextHandler(OnTextHandler onTextHandler) {
        this.edge.getOrCreateTextLocation().setOnTextHandler(onTextHandler);
        positionalIndex.setNode(nodeBuilder(positionalIndex.getNode()).hasTextHandler(true).build());
        binTree.setNode(positionalIndex);
        return this;
    }

    public BinEdgeBuilder textFilter(Predicate textFilter) {
        this.edge.getTextLocation().setTextFilter(textFilter);
        return this;
    }

    public BinEdge build() {
        return edge;
    }
}
