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
import dk.ott.core.dsl.expression.PathReference;
import dk.ott.core.dsl.expression.XpathParser;
import dk.ott.core.finder.SingleElementFinder;
import dk.ott.core.processing.ElementFinderReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootTreeBuilder {
    private ElementFinderReference elementFinderReference;
    private Map<String, PathReference> referenceStore;

    public RootTreeBuilder(ElementFinderReference elementFinderReference) {
        this.elementFinderReference = elementFinderReference;
        this.referenceStore = new HashMap<String, PathReference>();
    }

    public ElementTreeBuilder<RootTreeBuilder> path(String path) {
        List<TreePathAdder> adders = XpathParser.parseXpath(path, true);
        if (adders.isEmpty()) {
            throw new RuntimeException("The provided path seems to be empty: " + path);
        }
        PathReference currentPathReference = new PathReference(elementFinderReference, false);
        for (TreePathAdder treePathAdder : adders) {
            currentPathReference = treePathAdder.addTreePath(currentPathReference, true);
        }
        return new ElementTreeBuilder<RootTreeBuilder>(this, referenceStore, currentPathReference);
    }

    public ElementTreeBuilder<RootTreeBuilder> element(String elementName) {
        elementFinderReference.setSearchElement(elementName, false);
        return new ElementTreeBuilder<RootTreeBuilder>(this, referenceStore, new PathReference(elementFinderReference, elementName, false));
    }

    public ElementTreeBuilder<RootTreeBuilder> relativeElement(String elementName) {
        elementFinderReference.setSearchElement(elementName, true);
        return new ElementTreeBuilder<RootTreeBuilder>(this, referenceStore, new PathReference(elementFinderReference, elementName, true));
    }

    // Present for handlerBuilder symmetry
    public void build() {
    }

    public static RootTreeBuilder newTreeBuilder() {
        return new RootTreeBuilder(new SingleElementFinder().getReference());
    }
}
