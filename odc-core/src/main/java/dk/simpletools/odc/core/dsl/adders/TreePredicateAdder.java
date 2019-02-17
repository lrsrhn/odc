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
package dk.simpletools.odc.core.dsl.adders;

import dk.simpletools.odc.core.dsl.expression.PathReference;
import dk.simpletools.odc.core.dsl.searchtree.ExpressionHelper;
import dk.simpletools.odc.core.predicate.Predicate;

public class TreePredicateAdder implements TreePathAdder {
    private Predicate predicate;

    public TreePredicateAdder(Predicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public PathReference addTreePath(PathReference reference, boolean hasRoot) {
        return new PathReference(
                ExpressionHelper.addNextPredicate(reference).setPredicate(predicate),
                predicate,
                reference.isRelative()
        );
    }
}