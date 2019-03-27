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

import dk.ott.core.dsl.adders.TreePathAdder;
import dk.ott.core.dsl.expression.PathFragment;
import dk.ott.core.dsl.expression.PathReference;
import dk.ott.core.dsl.expression.XpathParser;
import dk.ott.core.finder.ElementFinder;
import dk.ott.core.finder.OnEndHandler;
import dk.ott.core.finder.OnStartHandler;
import dk.ott.core.predicate.Predicate;

import java.util.List;
import java.util.Map;

public class ElementTreeBuilder<T> {
    private T parentTreeBuilder;
    private PathReference parentReference;
    private Map<String, PathReference> referenceStore;

    ElementTreeBuilder(T parentTreeBuilder, Map<String, PathReference> referenceStore, PathReference parentReference) {
        this.parentTreeBuilder = parentTreeBuilder;
        this.parentReference = parentReference;
        this.referenceStore = referenceStore;
    }

    public ElementTreeBuilder<ElementTreeBuilder<T>> element(String elementName) {
        ElementFinder newElementFinder = ExpressionHelper.addNextElementFinder(parentReference)
                .setSearchElement(elementName, false);
        return new ElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, new PathReference(newElementFinder, elementName, false));
    }

    public OnlyEndTreeBuilder<T> all(OnStartHandler onStartHandler, OnEndHandler onEndHandler) {
        ElementFinder newElementFinder = ExpressionHelper.addNextAllFinder(parentReference);
        newElementFinder.buildSearchLocation(null, false)
                .onStartHandler(onStartHandler)
                .onEndHandler(onEndHandler)
                .build();
        return new OnlyEndTreeBuilder<T>(parentTreeBuilder, parentReference);
    }

    public ElementTreeBuilder<ElementTreeBuilder<T>> path(String path) {
        List<TreePathAdder> adders = XpathParser.parseXpath(path, false);
        if (adders.isEmpty()) {
            throw new RuntimeException("The provided path seems to be empty: " + path);
        }
        PathReference currentPathReference = parentReference;
        for (TreePathAdder treePathAdder : adders) {
            currentPathReference = treePathAdder.addTreePath(currentPathReference, false);
        }
        return new ElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, currentPathReference);
    }

    public ElementTreeBuilder<ElementTreeBuilder<T>> relativeElement(String elementName) {
        ElementFinder newElementFinder = ExpressionHelper.addNextElementFinder(parentReference)
                .setSearchElement(elementName, true);
        return new ElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, new PathReference(newElementFinder, elementName, true));
    }

    public PartialSearchLocationBuilder<ElementTreeBuilder<T>> onStart() {
        return new PartialSearchLocationBuilder<ElementTreeBuilder<T>>(this, ExpressionHelper.getSearchLocationBuilder(parentReference));
    }

    public PartialSearchTextLocationBuilder<ElementTreeBuilder<T>> onText() {
        return new PartialSearchTextLocationBuilder<ElementTreeBuilder<T>>(this, ExpressionHelper.getSearchLocationBuilder(parentReference));
    }

    public ElementTreeBuilder<T> storeReference(String referenceName) {
        this.referenceStore.put(referenceName, parentReference);
        return this;
    }

    public ElementTreeBuilder<T> recursionToReference(String referenceName) {
        PathReference reference = referenceStore.get(referenceName);
        if (reference == null) {
            throw new RuntimeException(String.format("The reference '%s' does not exist in the reference store", referenceName));
        }
        addReference(reference);
        return this;
    }

    private ElementTreeBuilder<T> addReference(PathReference reference) {
        ExpressionHelper.addElmentFinderCopy(parentReference, reference)
                .mergeElementFinder(reference.getElementFinder());
        return this;
    }

    public ElementTreeBuilder<T> addPathFragment(PathFragment pathFragment) {
        PathReference reference = pathFragment.getPathReference();
        ExpressionHelper.addElmentFinderCopy(parentReference, reference)
                .mergeElementFinder(reference.getElementFinder());
        return this;
    }

    public OnlyElementTreeBuilder<ElementTreeBuilder<T>> predicate(Predicate predicate) {
        ElementFinder newElementFinder = ExpressionHelper.addNextPredicate(parentReference)
                .setPredicate(predicate);
        return new OnlyElementTreeBuilder<ElementTreeBuilder<T>>(this, referenceStore, new PathReference(newElementFinder, predicate, false));
    }

    public T end() {
        return parentTreeBuilder;
    }

    public T end(OnEndHandler onEndHandler) {
        ExpressionHelper.getSearchLocationBuilder(parentReference)
                    .onEndHandler(onEndHandler)
                    .build();
        return parentTreeBuilder;
    }
}
