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
import dk.ott.core.dsl.expression.Element;
import dk.ott.core.dsl.expression.ElementPathParser;
import dk.ott.core.finder.ElementFinder;
import dk.ott.core.finder.SearchLocationBuilder;

import java.util.List;

import static dk.ott.core.dsl.expression.ElementPathParser.parseElementPath;

public class ExpressionHelper {

    public static ElementFinder addNextElementFinder(TreeEdgeReference parentReference) {
        return parentReference.getSearchLocationBuilder().addSearchElementFinder();
//        if (parentReference.getLastPredicate() != null) {
//            return parentReference.getElementFinder()
//                    .buildSearchLocation(parentReference.getLastPredicate())
//                    .addSearchElementFinder();
//        } else {
//            return parentReference.getElementFinder()
//                    .buildSearchLocation(parentReference.getLastSearchElement(), parentReference.isRelative())
//                    .addSearchElementFinder();
//        }
    }

    public static ElementFinder addNextAllFinder(TreeEdgeReference parentReference) {
        return parentReference.getSearchLocationBuilder().addAllElementFinder();
//        if (parentReference.getLastPredicate() != null) {
//            return parentReference.getElementFinder()
//                    .buildSearchLocation(parentReference.getLastPredicate())
//                    .addAllElementFinder();
//        } else {
//            return parentReference.getElementFinder()
//                    .buildSearchLocation(parentReference.getLastSearchElement(), parentReference.isRelative())
//                    .addAllElementFinder();
//        }
    }

    public static ElementFinder addNextPredicate(TreeEdgeReference parentReference) {
        return parentReference.getSearchLocationBuilder().addPredicateElementFinder();
//        if (parentReference.getLastPredicate() == null) {
//            return parentReference.getElementFinder()
//                    .buildSearchLocation(parentReference.getLastSearchElement(), parentReference.isRelative())
//                    .addPredicateElementFinder();
//        }
//        throw new IllegalStateException("Two predicate finder may not be adjacent");
    }

    public static ElementFinder addElmentFinderCopy(TreeEdgeReference parentReference, TreeEdgeReference reference) {
        return parentReference.getSearchLocationBuilder().addSearchElementFinder();
//        if (parentReference.getLastPredicate() != null) {
//            if (reference.getElementFinder().isPredicate()) {
//                throw new RuntimeException("Two predicate finders may not be adjacent!");
//            } else {
//                return parentReference.getElementFinder()
//                        .buildSearchLocation(parentReference.getLastPredicate())
//                        .addSearchElementFinder();
//            }
//        } else {
//            if (reference.getElementFinder().isPredicate()) {
//                throw new RuntimeException("Last predicate cannot be empty when isPredicate is true");
//            } else {
//                return parentReference.getElementFinder()
//                        .buildSearchLocation(parentReference.getLastSearchElement(), parentReference.isRelative())
//                        .addSearchElementFinder();
//            }
//        }
    }

    public static SearchLocationBuilder getSearchLocationBuilder(TreeEdgeReference parentReference) {
        return parentReference.getSearchLocationBuilder();
//        if (parentReference.getLastPredicate() != null) {
//            return parentReference.getElementFinder()
//                    .buildSearchLocation(parentReference.getLastPredicate());
//        } else {
//            return parentReference.getElementFinder()
//                    .buildSearchLocation(parentReference.getLastSearchElement(), parentReference.isRelative());
//        }
    }

    public static TreeEdgeReference parseElementPath(String elementPath, TreeEdgeReference treeEdgeReference, boolean firstIsRoot) {
        List<Element> elementList = ElementPathParser.parseElementPath(elementPath);
        int index = 0;
        if (firstIsRoot) {
            Element element = elementList.get(0);
            treeEdgeReference = new TreeEdgeReference(treeEdgeReference.getElementFinder().setSearchElement(element.getElement(), element.isRelative()), element.getElement(), element.isRelative());
            index++;
        }
        for (;index < elementList.size(); index++) {
            Element element = elementList.get(index);
          ElementFinder currentElementFinder = ExpressionHelper.addNextElementFinder(treeEdgeReference)
                    .setSearchElement(element.getElement(), element.isRelative());
          treeEdgeReference = new TreeEdgeReference(currentElementFinder, element.getElement(), element.isRelative());
        }
        return treeEdgeReference;
    }
}
