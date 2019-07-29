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
package dk.ott.core.dsl.searchtree;

import dk.ott.core.dsl.ObservableTreeFragment;
import dk.ott.core.dsl.TreeEdgeReference;
import dk.ott.core.event.OnEndHandler;
import dk.ott.core.event.OnStartHandler;
import dk.ott.core.event.OnTextHandler;
import dk.ott.core.predicate.Predicate;

import java.util.Map;

public class ElementTreeBuilder<T> {
    private T parentTreeBuilder;
    private TreeEdgeReference parentReference;
    private Map<String, TreeEdgeReference> referenceStore;

    ElementTreeBuilder(T parentTreeBuilder, Map<String, TreeEdgeReference> referenceStore, TreeEdgeReference parentReference) {
        this.parentTreeBuilder = parentTreeBuilder;
        this.parentReference = parentReference;
        this.referenceStore = referenceStore;
    }

    public ElementTreeBuilder<ElementTreeBuilder<T>> element(String elementName) {
        TreeEdgeReference treeEdgeReference = parentReference.getSearchLocationBuilder()
                .addSearchElementFinder(elementName, false)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, treeEdgeReference);
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
        TreeEdgeReference currentTreeEdgeReference = ExpressionHelper.parseElementPath(elementPath, parentReference, false);
        return new ElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, currentTreeEdgeReference);
    }

    public ElementTreeBuilder<ElementTreeBuilder<T>> relativeElement(String elementName) {
        TreeEdgeReference treeEdgeReference = parentReference.getSearchLocationBuilder()
                .addSearchElementFinder(elementName, true)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, treeEdgeReference);
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
        TreeEdgeReference reference = referenceStore.get(referenceName);
        if (reference == null) {
            throw new RuntimeException(String.format("The reference '%s' does not exist in the reference store", referenceName));
        }
        addReference(reference);
        return this;
    }

    private ElementTreeBuilder<T> addReference(TreeEdgeReference reference) {
        ExpressionHelper.addElementFinderSameAsReference(parentReference, reference)
                .mergeElementFinder(reference.getElementFinder());
        return this;
    }

    public ElementTreeBuilder<T> addPathFragment(ObservableTreeFragment observableTreeFragment) {
        TreeEdgeReference reference = observableTreeFragment.getTreeEdgeReference();
        ExpressionHelper.addElementFinderSameAsReference(parentReference, reference)
                .mergeElementFinder(reference.getElementFinder());
        return this;
    }

    public OnlyElementTreeBuilder<ElementTreeBuilder<T>> predicate(Predicate predicate) {
        TreeEdgeReference treeEdgeReference = parentReference.getSearchLocationBuilder()
                .addPredicateElementFinder(predicate)
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, treeEdgeReference);
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
