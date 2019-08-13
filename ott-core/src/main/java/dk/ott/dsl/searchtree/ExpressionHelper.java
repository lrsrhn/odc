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
import dk.ott.dsl.parser.Element;
import dk.ott.dsl.parser.ElementPathParser;
import dk.ott.core.Node;

import java.util.List;

public class ExpressionHelper {

    public static Node addElementFinderSameAsReference(EdgeReference parentReference, EdgeReference reference) {
        if (parentReference.isPredicate()) {
            if (reference.isPredicate()) {
                throw new RuntimeException("Two predicate finders may not be adjacent!");
            } else {
                return parentReference.getSearchLocationBuilder().addSearchElementFinder();
            }
        } else {
            if (reference.getElementFinder().isPredicate()) {
                return parentReference.getSearchLocationBuilder().addPredicateElementFinder();
            } else {
                return parentReference.getSearchLocationBuilder().addSearchElementFinder();
            }
        }
    }

    public static EdgeReference parseElementPath(String elementPath, EdgeReference edgeReference, boolean firstIsRoot) {
        List<Element> elementList = ElementPathParser.parseElementPath(elementPath);
        int index = 0;
        EdgeReference currentEdgeReference = edgeReference;
        if (firstIsRoot) {
            Element element = elementList.get(0);
            currentEdgeReference = currentEdgeReference.getElementFinder()
                    .buildEdge(element.getElement(), element.isRelative())
                    .toTreeEdgeReference();
            index++;
        }
        for (;index < elementList.size(); index++) {
            Element element = elementList.get(index);
            currentEdgeReference = currentEdgeReference.getSearchLocationBuilder()
                .addSearchElementFinder(element.getElement(), element.isRelative())
                .toTreeEdgeReference();
        }
        return currentEdgeReference;
    }
}
