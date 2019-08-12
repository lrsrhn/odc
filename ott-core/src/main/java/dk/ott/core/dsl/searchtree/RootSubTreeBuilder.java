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

import dk.ott.core.dsl.TreeEdgeReference;
import dk.ott.core.finder.ElementFinder;
import dk.ott.core.finder.SingleElementFinder;
import dk.ott.core.finder.SinglePredicateMatchFinder;
import dk.ott.core.predicate.Predicate;
import dk.ott.core.predicate.Predicates;

import java.util.HashMap;
import java.util.Map;

/**
 * Used when creating an isolated sub tree which may be added to another tree.
 * This builder allows predicates as root finder.
 */
public class RootSubTreeBuilder {
    private Map<String, TreeEdgeReference> referenceStore;
    private ElementFinder rootElementFinder;

    public RootSubTreeBuilder(ElementFinder elementFinder) {
        this.referenceStore = new HashMap<String, TreeEdgeReference>();
        this.rootElementFinder = elementFinder;
    }

    public ElementTreeBuilder<RootSubTreeBuilder> element(String elementName) {
        TreeEdgeReference treeEdgeReference = rootElementFinder.buildSearchLocation(elementName, false)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public ElementTreeBuilder<RootSubTreeBuilder> elementPath(String elementPath) {
        TreeEdgeReference currentTreeEdgeReference = ExpressionHelper.parseElementPath(elementPath, new TreeEdgeReference(rootElementFinder), true);
        return new ElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, currentTreeEdgeReference);
    }

    public ElementTreeBuilder<RootSubTreeBuilder> relativeElement(String elementName) {
        TreeEdgeReference treeEdgeReference = rootElementFinder
                .buildSearchLocation(elementName, true)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    RootSubTreeBuilder addReference(TreeEdgeReference treeEdgeReference) {
        rootElementFinder.mergeElementFinder(treeEdgeReference.getElementFinder());
        return this;
    }

    public OnlyElementTreeBuilder<RootSubTreeBuilder> predicate(Predicate predicate) {
        TreeEdgeReference treeEdgeReference = rootElementFinder
                .buildSearchLocation(predicate)
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public OnlyElementTreeBuilder<RootSubTreeBuilder> namespace(String namespace) {
        TreeEdgeReference treeEdgeReference = rootElementFinder
                .buildSearchLocation(Predicates.namespace(namespace))
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public OnlyElementTreeBuilder<RootSubTreeBuilder> noNamespace() {
        TreeEdgeReference treeEdgeReference = rootElementFinder
                .buildSearchLocation(Predicates.noNamespace())
                .toTreeEdgeReference();
        return new OnlyElementTreeBuilder<RootSubTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public TreeEdgeReference end() {
        return new TreeEdgeReference(rootElementFinder);
    }
}
