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

import dk.ott.core.dsl.ObservableRootTreeFragment;
import dk.ott.core.dsl.ObservableTreeFragment;
import dk.ott.core.dsl.TreeEdgeReference;
import dk.ott.core.event.OnEndHandler;
import dk.ott.core.predicate.Predicate;
import dk.ott.core.predicate.Predicates;

import java.util.HashMap;
import java.util.Map;

public class SubTreeBuilder {
    private TreeEdgeReference rootReference;
    private Map<String, TreeEdgeReference> referenceStore;

    public SubTreeBuilder(Map<String, TreeEdgeReference> referenceStore, TreeEdgeReference treeEdgeReference) {
        this.referenceStore = referenceStore;
        this.rootReference = treeEdgeReference;
    }

    public SubTreeBuilder(TreeEdgeReference parentReference) {
        this(new HashMap<String, TreeEdgeReference>(), parentReference);
    }

    public ElementTreeBuilder<SubTreeBuilder> element(String elementName) {
        TreeEdgeReference treeEdgeReference = rootReference.getSearchLocationBuilder()
                .addSearchElementFinder(elementName, false)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<SubTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public ElementTreeBuilder<SubTreeBuilder> elementPath(String elementPath) {
        TreeEdgeReference currentTreeEdgeReference = ExpressionHelper.parseElementPath(elementPath, rootReference, false);
        return new ElementTreeBuilder<SubTreeBuilder>(this, referenceStore, currentTreeEdgeReference);
    }

    public ElementTreeBuilder<SubTreeBuilder> relativeElement(String elementName) {
        TreeEdgeReference treeEdgeReference = rootReference.getSearchLocationBuilder()
                .addSearchElementFinder(elementName, true)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<SubTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public PartialSearchTextLocationBuilder<SubTreeBuilder> onText() {
        return new PartialSearchTextLocationBuilder<SubTreeBuilder>(this, rootReference.getSearchLocationBuilder());
    }

    public PartialSearchLocationBuilder<SubTreeBuilder> onStart() {
        return new PartialSearchLocationBuilder<SubTreeBuilder>(this, rootReference.getSearchLocationBuilder());
    }

    public SubTreeBuilder storeReference(String referenceName) {
        this.referenceStore.put(referenceName, rootReference);
        return this;
    }

    public SubTreeBuilder recursionToReference(String referenceName) {
        TreeEdgeReference reference = referenceStore.get(referenceName);
        if (reference == null) {
            throw new RuntimeException(String.format("The reference '%s' does not exist in the reference store", referenceName));
        }
        addReference(reference);
        return this;
    }

    SubTreeBuilder addReference(TreeEdgeReference treeEdgeReference) {
        rootReference.getElementFinder().mergeElementFinder(treeEdgeReference.getElementFinder());
        return this;
    }

    public SubTreeBuilder addSubTree(ObservableRootTreeFragment observableRootTreeFragment) {
        TreeEdgeReference reference = observableRootTreeFragment.getTreeEdgeReference();
        ExpressionHelper.addElementFinderSameAsReference(rootReference, reference)
                .mergeElementFinder(reference.getElementFinder());
        return this;
    }

    public OnlyElementTreeBuilder<SubTreeBuilder> predicate(Predicate predicate) {
        TreeEdgeReference treeEdgeReference = rootReference.getSearchLocationBuilder()
                .addPredicateElementFinder(predicate)
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<SubTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public OnlyElementTreeBuilder<SubTreeBuilder> namespace(String namespace) {
        TreeEdgeReference treeEdgeReference = rootReference.getSearchLocationBuilder()
                .addPredicateElementFinder(Predicates.namespace(namespace))
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<SubTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public OnlyElementTreeBuilder<SubTreeBuilder> noNamespace() {
        TreeEdgeReference treeEdgeReference = rootReference.getSearchLocationBuilder()
                .addPredicateElementFinder(Predicates.noNamespace())
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<SubTreeBuilder>(this, referenceStore, treeEdgeReference);
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
