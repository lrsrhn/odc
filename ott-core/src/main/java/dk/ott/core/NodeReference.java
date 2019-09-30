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

import dk.ott.dsl.expression.SearchLocationReference;
import dk.ott.predicate.Predicate;
import dk.ott.processing.ObjectStore;
import dk.ott.processing.ElementCursor;

import java.util.List;
import java.util.Set;

public class NodeReference implements Node {
    private Node node;

    public NodeReference(Node node) {
        this.node = node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public EdgeBuilder buildEdge(String searchElement, boolean isRelative) {
        return node.buildEdge(searchElement, isRelative);
    }

    @Override
    public EdgeBuilder buildEdge(Predicate predicate) {
        return node.buildEdge(predicate);
    }

    @Override
    public NodeReference getReference() {
        return this;
    }

    @Override
    public Node getDereference() {
        return node;
    }

    @Override
    public void buildToString(StringBuilder previousNodesStringBuilder, Set<Node> visited, StringBuilder toStringBuilder) {
        node.buildToString(previousNodesStringBuilder, visited, toStringBuilder);
    }

    @Override
    public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
        return node.lookupEdge(elementCursor, objectStore, includeAbsolutes);
    }

    @Override
    public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore) {
        return node.lookupEdge(elementCursor, objectStore);
    }

    @Override
    public List<SearchLocationReference> getSeachLocationReferences() {
        return node.getSeachLocationReferences();
    }

    @Override
    public void mergeNode(Node nodeToMerge) {
        this.node.mergeNode(nodeToMerge);
    }

    @Override
    public boolean isPredicate() {
        return node.isPredicate();
    }

    @Override
    public boolean hasRelative() {
        return node.hasRelative();
    }

    @Override
    public Edge getOtherwise() {
        return node.getOtherwise();
    }

    @Override
    public void setOtherwise(Edge otherwise) {
        node.setOtherwise(otherwise);
    }

    @Override
    public void unreferenceTree() {
        node.unreferenceTree();
    }
}
