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
import dk.ott.core.predicate.Predicate;

import java.util.Map;

public class RootAllTreeBuilder {
    private TreeEdgeReference rootReference;
    private Map<String, TreeEdgeReference> referenceStore;

    public RootAllTreeBuilder(Map<String, TreeEdgeReference> referenceStore, TreeEdgeReference parentReference) {
        this.referenceStore = referenceStore;
        this.rootReference = parentReference;
    }

    public ElementTreeBuilder<RootAllTreeBuilder> element(String elementName) {
        TreeEdgeReference treeEdgeReference = rootReference.getSearchLocationBuilder()
                .addSearchElementFinder(elementName, false)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<RootAllTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public ElementTreeBuilder<RootAllTreeBuilder> elementPath(String elementPath) {
        TreeEdgeReference currentTreeEdgeReference = ExpressionHelper.parseElementPath(elementPath, rootReference, false);
        return new ElementTreeBuilder<RootAllTreeBuilder>(this, referenceStore, currentTreeEdgeReference);
    }

    public ElementTreeBuilder<RootAllTreeBuilder> relativeElement(String elementName) {
        TreeEdgeReference treeEdgeReference = rootReference.getSearchLocationBuilder()
                .addSearchElementFinder(elementName, true)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<RootAllTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public PartialSearchTextLocationBuilder<RootAllTreeBuilder> onText() {
        return new PartialSearchTextLocationBuilder<RootAllTreeBuilder>(this, rootReference.getSearchLocationBuilder());
    }

    public PartialSearchLocationBuilder<RootAllTreeBuilder> onStart() {
        return new PartialSearchLocationBuilder<RootAllTreeBuilder>(this, rootReference.getSearchLocationBuilder());
    }

    public RootAllTreeBuilder storeReference(String referenceName) {
        this.referenceStore.put(referenceName, rootReference);
        return this;
    }

    public RootAllTreeBuilder recursionToReference(String referenceName) {
        TreeEdgeReference reference = referenceStore.get(referenceName);
        if (reference == null) {
            throw new RuntimeException(String.format("The reference '%s' does not exist in the reference store", referenceName));
        }
        addReference(reference);
        return this;
    }

    RootAllTreeBuilder addReference(TreeEdgeReference reference) {
        ExpressionHelper.addElementFinderSameAsReference(rootReference, reference)
                .mergeElementFinder(reference.getElementFinder());
        return this;
    }

    public RootAllTreeBuilder addObservableTreeFragment(ObservableTreeFragment observableTreeFragment) {
        TreeEdgeReference reference = observableTreeFragment.getTreeEdgeReference();
        ExpressionHelper.addElementFinderSameAsReference(rootReference, reference)
                .mergeElementFinder(reference.getElementFinder());
        return this;
    }

    public OnlyElementTreeBuilder<RootAllTreeBuilder> predicate(Predicate predicate) {
        TreeEdgeReference treeEdgeReference = rootReference.getSearchLocationBuilder()
                .addPredicateElementFinder(predicate)
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<RootAllTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public TreeEdgeReference end(OnEndHandler onEndHandler) {
        rootReference.getSearchLocationBuilder()
                    .onEndHandler(onEndHandler)
                    .build();
        return rootReference;
    }

    public TreeEdgeReference end() {
        return rootReference;
    }
}
