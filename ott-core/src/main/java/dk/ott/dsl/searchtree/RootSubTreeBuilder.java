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
package dk.ott.dsl.searchtree;

import dk.ott.dsl.EdgeReference;
import dk.ott.core.Node;
import dk.ott.predicate.Predicate;
import dk.ott.predicate.Predicates;

import java.util.HashMap;
import java.util.Map;

/**
 * Used when creating an isolated sub tree which may be added to another tree.
 * This builder allows predicates as root finder.
 */
public class RootSubTreeBuilder {
    private Map<String, EdgeReference> referenceStore;
    private Node rootNode;

    public RootSubTreeBuilder(Node node) {
        this.referenceStore = new HashMap<String, EdgeReference>();
        this.rootNode = node;
    }

    public ElementTreeBuilder<RootSubTreeBuilder> element(String elementName) {
        EdgeReference edgeReference = rootNode.buildEdge(elementName, false)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, edgeReference);
    }

    public ElementTreeBuilder<RootSubTreeBuilder> elementPath(String elementPath) {
        EdgeReference currentEdgeReference = ExpressionHelper.parseElementPath(elementPath, new EdgeReference(rootNode), true);
        return new ElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, currentEdgeReference);
    }

    public ElementTreeBuilder<RootSubTreeBuilder> relativeElement(String elementName) {
        EdgeReference edgeReference = rootNode
                .buildEdge(elementName, true)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, edgeReference);
    }

    RootSubTreeBuilder addReference(EdgeReference edgeReference) {
        rootNode.mergeNode(edgeReference.getElementFinder());
        return this;
    }

    public OnlyElementTreeBuilder<RootSubTreeBuilder> predicate(Predicate predicate) {
        EdgeReference edgeReference = rootNode
                .buildEdge(predicate)
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, edgeReference);
    }

    public OnlyElementTreeBuilder<RootSubTreeBuilder> namespace(String namespace) {
        EdgeReference edgeReference = rootNode
                .buildEdge(Predicates.namespace(namespace))
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, edgeReference);
    }

    public OnlyElementTreeBuilder<RootSubTreeBuilder> noNamespace() {
        EdgeReference edgeReference = rootNode
                .buildEdge(Predicates.noNamespace())
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, edgeReference);
    }

    public EdgeReference end() {
        return new EdgeReference(rootNode);
    }
}
