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
package dk.simpletools.odc.core.dsl.searchtree;

import dk.simpletools.odc.core.dsl.expression.PathReference;
import dk.simpletools.odc.core.dsl.expression.TreePathAdder;
import dk.simpletools.odc.core.finder.OnEndHandler;
import dk.simpletools.odc.core.dsl.expression.XpathParser;
import dk.simpletools.odc.core.finder.ElementFinder;
import dk.simpletools.odc.core.predicate.Predicate;

import java.util.List;
import java.util.Map;

import static dk.simpletools.odc.core.dsl.searchtree.ExpressionHelper.*;

public class RootAllTreeBuilder {
    private PathReference rootReference;
    private Map<String, PathReference> referenceStore;

    public RootAllTreeBuilder(Map<String, PathReference> referenceStore, PathReference parentReference) {
        this.referenceStore = referenceStore;
        this.rootReference = parentReference;
    }

    public ElementTreeBuilder<RootAllTreeBuilder> element(String elementName) {
        ElementFinder newElementFinder = addNextElementFinder(rootReference)
                .setSearchElement(elementName, false);
        return new ElementTreeBuilder<RootAllTreeBuilder>(this, referenceStore, new PathReference(newElementFinder, elementName, false));
    }

    public ElementTreeBuilder<RootAllTreeBuilder> path(String path) {
        List<TreePathAdder> adders = XpathParser.parseXpath(path, false);
        if (adders.isEmpty()) {
            throw new RuntimeException("The provided path seems to be empty: " + path);
        }
        PathReference currentPathReference = rootReference;
        for (TreePathAdder treePathAdder : adders) {
            currentPathReference = treePathAdder.addTreePath(currentPathReference, false);
        }
        return new ElementTreeBuilder<RootAllTreeBuilder>(this, referenceStore, currentPathReference);
    }

    public ElementTreeBuilder<RootAllTreeBuilder> relativeElement(String elementName) {
        ElementFinder newElementFinder = addNextElementFinder(rootReference)
                .setSearchElement(elementName, true);
        return new ElementTreeBuilder<RootAllTreeBuilder>(this, referenceStore, new PathReference(newElementFinder, elementName, true));
    }

    public PartialSearchLocationBuilder<RootAllTreeBuilder> observeBy() {
        return new PartialSearchLocationBuilder<RootAllTreeBuilder>(this, getSearchLocationBuilder(rootReference));
    }

    public RootAllTreeBuilder storeReference(String referenceName) {
        this.referenceStore.put(referenceName, rootReference);
        return this;
    }

    public RootAllTreeBuilder recursionToReference(String referenceName) {
        PathReference reference = referenceStore.get(referenceName);
        if (reference == null) {
            throw new RuntimeException(String.format("The reference '%s' does not exist in the reference store", referenceName));
        }
        addReference(reference);
        return this;
    }

    public RootAllTreeBuilder addReference(PathReference reference) {
        addElmentFinderCopy(rootReference, reference)
                .mergeElementFinder(reference.getElementFinder());
        return this;
    }

    public OnlyElementTreeBuilder<RootAllTreeBuilder> predicate(Predicate predicate) {
        ElementFinder newElementFinder = addNextPredicate(rootReference)
                .setPredicate(predicate);
        return new OnlyElementTreeBuilder<RootAllTreeBuilder>(this, referenceStore, new PathReference(newElementFinder, predicate, false));
    }

    public PathReference end(OnEndHandler onEndHandler) {
        getSearchLocationBuilder(rootReference)
                    .onEndHandler(onEndHandler)
                    .build();
        return rootReference;
    }

    public PathReference end() {
        return rootReference;
    }
}
