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

import java.util.Map;

public class OnlyElementTreeBuilder<T> {
    private T parentTreeBuilder;
    private EdgeReference parentReference;
    private Map<String, EdgeReference> referenceStore;

    public OnlyElementTreeBuilder(T parentTreeBuilder, Map<String, EdgeReference> referenceStore, EdgeReference parentReference) {
        this.parentTreeBuilder = parentTreeBuilder;
        this.parentReference = parentReference;
        this.referenceStore = referenceStore;
    }

    public ElementTreeBuilder<OnlyElementTreeBuilder<T>> element(String elementName) {
        EdgeReference edgeReference = parentReference.getEdgeBuilder()
                .addElementChildNode(elementName, false)
                .toEdgeReference();
        return new ElementTreeBuilder<OnlyElementTreeBuilder<T>>(this, referenceStore, edgeReference);
    }

    public ElementTreeBuilder<OnlyElementTreeBuilder<T>> otherwise() {
        EdgeReference edgeReference = parentReference.getOtherwiseBuilder()
                .toEdgeReference();
        return new ElementTreeBuilder<OnlyElementTreeBuilder<T>>(this, referenceStore, edgeReference);
    }

    public ElementTreeBuilder<OnlyElementTreeBuilder<T>> relativeElement(String elementName) {
        EdgeReference edgeReference = parentReference.getEdgeBuilder()
                .addElementChildNode(elementName, true)
                .toEdgeReference();
        return new ElementTreeBuilder<OnlyElementTreeBuilder<T>>(this, referenceStore, edgeReference);
    }

    public OnlyElementTreeBuilder<T> storeReference(String referenceName) {
        this.referenceStore.put(referenceName, parentReference);
        return this;
    }

    public OnlyElementTreeBuilder<T> recursionToReference(String referenceName) {
        EdgeReference reference = referenceStore.get(referenceName);
        if (reference == null) {
            throw new RuntimeException(String.format("The reference '%s' does not exist in the reference store", referenceName));
        }
        addReference(reference);
        return this;
    }

    private OnlyElementTreeBuilder<T> addReference(EdgeReference reference) {
        ExpressionHelper.addChildNodeFromReference(parentReference, reference)
                .mergeNode(reference.getNode());
        return this;
    }

    public OnlyElementTreeBuilder<T> addTreeFragment(ObservableRootTreeFragment observableTreeFragment) {
        EdgeReference reference = observableTreeFragment.getTreeEdgeReference();
        ExpressionHelper.addChildNodeFromReference(parentReference, reference)
                .mergeNode(reference.getNode());
        return this;
    }

    public PartialSearchTextLocationBuilder<OnlyElementTreeBuilder<T>> onText() {
        return new PartialSearchTextLocationBuilder<OnlyElementTreeBuilder<T>>(this, parentReference.getEdgeBuilder());
    }

    public PartialSearchLocationBuilder<OnlyElementTreeBuilder<T>> onStart() {
        return new PartialSearchLocationBuilder<OnlyElementTreeBuilder<T>>(this, parentReference.getEdgeBuilder());
    }

    public T end() {
        return parentTreeBuilder;
    }

    public T end(OnEndHandler onEndHandler) {
        parentReference.getEdgeBuilder()
                .onEndHandler(onEndHandler)
                .build();
        return parentTreeBuilder;
    }
}
