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

import dk.ott.dsl.ObservableRootTreeFragment;
import dk.ott.dsl.EdgeReference;
import dk.ott.event.OnEndHandler;
import dk.ott.predicate.Predicate;
import dk.ott.predicate.Predicates;

import java.util.Map;

public class SubTreeBuilder {
    private EdgeReference rootReference;
    private Map<String, EdgeReference> referenceStore;

    public SubTreeBuilder(Map<String, EdgeReference> referenceStore, EdgeReference edgeReference) {
        this.referenceStore = referenceStore;
        this.rootReference = edgeReference;
    }

    public ElementTreeBuilder<SubTreeBuilder> element(String elementName) {
        EdgeReference edgeReference = rootReference.getEdgeBuilder()
                .addElementChildNode(elementName, false)
                .toEdgeReference();
        return new ElementTreeBuilder<SubTreeBuilder>(this, referenceStore, edgeReference);
    }

    public ElementTreeBuilder<SubTreeBuilder> elementPath(String elementPath) {
        EdgeReference currentEdgeReference = ExpressionHelper.parseElementPath(elementPath, rootReference, false);
        return new ElementTreeBuilder<SubTreeBuilder>(this, referenceStore, currentEdgeReference);
    }

    public ElementTreeBuilder<SubTreeBuilder> relativeElement(String elementName) {
        EdgeReference edgeReference = rootReference.getEdgeBuilder()
                .addElementChildNode(elementName, true)
                .toEdgeReference();
        return new ElementTreeBuilder<SubTreeBuilder>(this, referenceStore, edgeReference);
    }

    public PartialSearchTextLocationBuilder<SubTreeBuilder> onText() {
        return new PartialSearchTextLocationBuilder<SubTreeBuilder>(this, rootReference.getEdgeBuilder());
    }

    public PartialSearchLocationBuilder<SubTreeBuilder> onStart() {
        return new PartialSearchLocationBuilder<SubTreeBuilder>(this, rootReference.getEdgeBuilder());
    }

    public SubTreeBuilder storeReference(String referenceName) {
        this.referenceStore.put(referenceName, rootReference);
        return this;
    }

    public SubTreeBuilder recursionToReference(String referenceName) {
        EdgeReference reference = referenceStore.get(referenceName);
        if (reference == null) {
            throw new RuntimeException(String.format("The reference '%s' does not exist in the reference store", referenceName));
        }
        addReference(reference);
        return this;
    }

    SubTreeBuilder addReference(EdgeReference edgeReference) {
        rootReference.getNode().mergeNode(edgeReference.getNode());
        return this;
    }

    public SubTreeBuilder addSubTree(ObservableRootTreeFragment observableRootTreeFragment) {
        EdgeReference reference = observableRootTreeFragment.getTreeEdgeReference();
        ExpressionHelper.addChildNodeFromReference(rootReference, reference)
                .mergeNode(reference.getNode());
        return this;
    }

    public OnlyElementTreeBuilder<SubTreeBuilder> predicate(Predicate predicate) {
        EdgeReference edgeReference = rootReference.getEdgeBuilder()
                .addPredicateChildNode(predicate)
                .toEdgeReference();
        return new OnlyElementTreeBuilder<SubTreeBuilder>(this, referenceStore, edgeReference);
    }

    public OnlyElementTreeBuilder<SubTreeBuilder> namespace(String namespace) {
        EdgeReference edgeReference = rootReference.getEdgeBuilder()
                .addPredicateChildNode(Predicates.namespace(namespace))
                .toEdgeReference();
        return new OnlyElementTreeBuilder<SubTreeBuilder>(this, referenceStore, edgeReference);
    }

    public OnlyElementTreeBuilder<SubTreeBuilder> noNamespace() {
        EdgeReference edgeReference = rootReference.getEdgeBuilder()
                .addPredicateChildNode(Predicates.noNamespace())
                .toEdgeReference();
        return new OnlyElementTreeBuilder<SubTreeBuilder>(this, referenceStore, edgeReference);
    }

    public EdgeReference end(OnEndHandler onEndHandler) {
        rootReference.getEdgeBuilder()
                    .onEndHandler(onEndHandler)
                    .build();
        return rootReference;
    }

    public EdgeReference end() {
        return rootReference;
    }
}
