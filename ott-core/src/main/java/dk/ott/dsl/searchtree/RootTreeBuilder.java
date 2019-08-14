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

import dk.ott.dsl.EdgeReference;
import dk.ott.core.NodeReference;

import java.util.HashMap;
import java.util.Map;

public class RootTreeBuilder {
    private NodeReference nodeReference;
    private Map<String, EdgeReference> referenceStore;

    public RootTreeBuilder(NodeReference nodeReference) {
        this.nodeReference = nodeReference;
        this.referenceStore = new HashMap<String, EdgeReference>();
    }

    public ElementTreeBuilder<RootTreeBuilder> elementPath(String elementPath) {
        EdgeReference edgeReference = new EdgeReference(nodeReference);
        edgeReference = ExpressionHelper.parseElementPath(elementPath, edgeReference, true);
        return new ElementTreeBuilder<RootTreeBuilder>(this, referenceStore, edgeReference);
    }

    public ElementTreeBuilder<RootTreeBuilder> element(String elementName) {
        EdgeReference edgeReference = nodeReference.buildEdge(elementName, false)
                .toEdgeReference();
        return new ElementTreeBuilder<RootTreeBuilder>(this, referenceStore, edgeReference);
    }

    public ElementTreeBuilder<RootTreeBuilder> relativeElement(String elementName) {
        EdgeReference edgeReference = nodeReference.buildEdge(elementName, true)
            .toEdgeReference();
        return new ElementTreeBuilder<RootTreeBuilder>(this, referenceStore, edgeReference);
    }

    // Present for handlerBuilder symmetry
    public void build() {
    }
}
