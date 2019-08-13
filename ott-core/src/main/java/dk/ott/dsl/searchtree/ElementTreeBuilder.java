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
import dk.ott.event.OnStartHandler;
import dk.ott.event.OnTextHandler;
import dk.ott.predicate.Predicate;
import dk.ott.predicate.Predicates;

import java.util.Map;

public class ElementTreeBuilder<T> {
    private T parentTreeBuilder;
    private EdgeReference parentReference;
    private Map<String, EdgeReference> referenceStore;

    ElementTreeBuilder(T parentTreeBuilder, Map<String, EdgeReference> referenceStore, EdgeReference parentReference) {
        this.parentTreeBuilder = parentTreeBuilder;
        this.parentReference = parentReference;
        this.referenceStore = referenceStore;
    }

    public ElementTreeBuilder<ElementTreeBuilder<T>> element(String elementName) {
        EdgeReference edgeReference = parentReference.getSearchLocationBuilder()
                .addSearchElementFinder(elementName, false)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, edgeReference);
    }

    public OnlyEndTreeBuilder<T> all(OnStartHandler onStartHandler, OnTextHandler onTextHandler, OnEndHandler onEndHandler) {
        parentReference.getSearchLocationBuilder()
                .addAllElementFinder()
                    .onStartHandler(onStartHandler)
                    .onTextHandler(onTextHandler)
                    .onEndHandler(onEndHandler)
                    .build();
        return new OnlyEndTreeBuilder<T>(parentTreeBuilder, parentReference);
    }

    public ElementTreeBuilder<ElementTreeBuilder<T>> elementPath(String elementPath) {
        EdgeReference currentEdgeReference = ExpressionHelper.parseElementPath(elementPath, parentReference, false);
        return new ElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, currentEdgeReference);
    }

    public ElementTreeBuilder<ElementTreeBuilder<T>> relativeElement(String elementName) {
        EdgeReference edgeReference = parentReference.getSearchLocationBuilder()
                .addSearchElementFinder(elementName, true)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, edgeReference);
    }

    public PartialSearchLocationBuilder<ElementTreeBuilder<T>> onStart() {
        return new PartialSearchLocationBuilder<ElementTreeBuilder<T>>(this, parentReference.getSearchLocationBuilder());
    }

    public PartialSearchTextLocationBuilder<ElementTreeBuilder<T>> onText() {
        return new PartialSearchTextLocationBuilder<ElementTreeBuilder<T>>(this, parentReference.getSearchLocationBuilder());
    }

    public ElementTreeBuilder<T> storeReference(String referenceName) {
        this.referenceStore.put(referenceName, parentReference);
        return this;
    }

    public ElementTreeBuilder<T> recursionToReference(String referenceName) {
        EdgeReference reference = referenceStore.get(referenceName);
        if (reference == null) {
            throw new RuntimeException(String.format("The reference '%s' does not exist in the reference store", referenceName));
        }
        addReference(reference);
        return this;
    }

    private ElementTreeBuilder<T> addReference(EdgeReference reference) {
        ExpressionHelper.addElementFinderSameAsReference(parentReference, reference)
                .mergeNode(reference.getElementFinder());
        return this;
    }

    public ElementTreeBuilder<T> addTreeFragment(ObservableRootTreeFragment observableRootTreeFragment) {
        EdgeReference reference = observableRootTreeFragment.getTreeEdgeReference();
        ExpressionHelper.addElementFinderSameAsReference(parentReference, reference)
                .mergeNode(reference.getElementFinder());
        return this;
    }

    public OnlyElementTreeBuilder<ElementTreeBuilder<T>> predicate(Predicate predicate) {
        EdgeReference edgeReference = parentReference.getSearchLocationBuilder()
                .addPredicateElementFinder(predicate)
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, edgeReference);
    }

    public OnlyElementTreeBuilder<ElementTreeBuilder<T>> namespace(String namespace) {
        EdgeReference edgeReference = parentReference.getSearchLocationBuilder()
                .addPredicateElementFinder(Predicates.namespace(namespace))
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, edgeReference);
    }

    public OnlyElementTreeBuilder<ElementTreeBuilder<T>> noNamespace() {
        EdgeReference edgeReference = parentReference.getSearchLocationBuilder()
                .addPredicateElementFinder(Predicates.noNamespace())
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, edgeReference);
    }

    public T end() {
        return parentTreeBuilder;
    }

    public T end(OnEndHandler onEndHandler) {
        parentReference.getSearchLocationBuilder()
                    .onEndHandler(onEndHandler)
                    .build();
        return parentTreeBuilder;
    }
}
