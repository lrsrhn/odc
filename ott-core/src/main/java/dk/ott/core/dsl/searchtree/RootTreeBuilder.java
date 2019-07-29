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
import dk.ott.core.finder.ElementFinderReference;
import dk.ott.core.finder.SingleElementFinder;

import java.util.HashMap;
import java.util.Map;

public class RootTreeBuilder {
    private ElementFinderReference elementFinderReference;
    private Map<String, TreeEdgeReference> referenceStore;

    public RootTreeBuilder(ElementFinderReference elementFinderReference) {
        this.elementFinderReference = elementFinderReference;
        this.referenceStore = new HashMap<String, TreeEdgeReference>();
    }

    public ElementTreeBuilder<RootTreeBuilder> elementPath(String elementPath) {
        TreeEdgeReference treeEdgeReference = new TreeEdgeReference(elementFinderReference, null, false);
        treeEdgeReference = ExpressionHelper.parseElementPath(elementPath, treeEdgeReference, true);
        return new ElementTreeBuilder<RootTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public ElementTreeBuilder<RootTreeBuilder> element(String elementName) {
        TreeEdgeReference treeEdgeReference = elementFinderReference.buildSearchLocation(elementName, false)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<RootTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    public ElementTreeBuilder<RootTreeBuilder> relativeElement(String elementName) {
                TreeEdgeReference treeEdgeReference = elementFinderReference.buildSearchLocation(elementName, true)
                .toTreeEdgeReference();
        return new ElementTreeBuilder<RootTreeBuilder>(this, referenceStore, treeEdgeReference);
    }

    // Present for handlerBuilder symmetry
    public void build() {
    }

    public static RootTreeBuilder newTreeBuilder() {
        return new RootTreeBuilder(new SingleElementFinder().getReference());
    }
}
