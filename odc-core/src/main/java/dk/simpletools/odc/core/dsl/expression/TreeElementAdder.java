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
package dk.simpletools.odc.core.dsl.expression;

import dk.simpletools.odc.core.dsl.searchtree.ExpressionHelper;
import dk.simpletools.odc.core.finder.ElementFinder;

public class TreeElementAdder implements TreePathAdder {
    private final boolean isRelative;
    private String[] elements;

    public TreeElementAdder(String[] elements, boolean isRelative) {
        this.elements = elements;
        this.isRelative = isRelative;
    }

    @Override
    public PathReference addTreePath(PathReference reference, boolean hasRoot) {
        if (elements == null || elements.length == 0) {
            throw new RuntimeException("The elements list is null or empty!");
        }
        ElementFinder currentElementFinder = ExpressionHelper.addNextElementFinder(reference)
                .setSearchElement(elements[0], isRelative);
        if (elements.length > 1) {
            for (int i = 0; i < elements.length - 1; i++) {
                currentElementFinder = currentElementFinder.buildSearchLocation(elements[i], isRelative).addSearchElementFinder();
            }
            currentElementFinder = currentElementFinder.setSearchElement(elements[elements.length - 1], isRelative);
        }

        return new PathReference(currentElementFinder, elements[elements.length - 1], isRelative);
    }
}
