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
import dk.ott.util.TreePrettyPrintHelper;

import java.util.List;
import java.util.Set;

/**
 * Equivalent to Xpath /*
 *
 * All elements in the subtree are sent to the event handlers in the edge.
 */
public class NodeStar extends NodeBase {
    private Edge edge;

    public NodeStar() {
        super();
        this.edge = new Edge(true);
    }

    @Override
    public EdgeBuilder buildEdge(String searchElement, boolean isRelative) {
        return new EdgeBuilder(this, edge);
    }

    @Override
    public EdgeBuilder buildEdge(Predicate predicate) {
        return new EdgeBuilder(this, edge);
    }

    @Override
    public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
        return edge;
    }

    @Override
    public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore) {
        return edge;
    }

    @Override
    public List<SearchLocationReference> getSeachLocationReferences() {
        throw new UnsupportedOperationException("Not supported by All element finder");
    }

    @Override
    public void mergeNode(Node nodeToMerge) {
        throw new UnsupportedOperationException("Not supported by All element finder");
    }

    @Override
    public void buildToString(StringBuilder previousNodesStringBuilder, Set<Node> visited, StringBuilder toStringBuilder) {
        previousNodesStringBuilder.append("/*");
        TreePrettyPrintHelper.printSearchLocation(edge, previousNodesStringBuilder, visited, toStringBuilder);
    }

    @Override
    public boolean isPredicate() {
        return false;
    }

    // Returning true will make ObservableTreeTraveser not skip any elements
    @Override
    public boolean hasRelative() {
        return true;
    }

    @Override
    public void unreferenceTree() {
        if (edge.getChildNode() != null) {
          Node node = edge.getChildNode().getDereference();
          edge.setChildNode(node);
          node.unreferenceTree();
        }
    }
}
