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

import dk.ott.core.event.OnStartHandler;
import dk.ott.core.finder.SearchLocationBuilder;
import dk.ott.core.predicate.Predicate;

public class PartialSearchLocationBuilder<T> {
    private T parentTreeBuilder;
    private SearchLocationBuilder searchLocationBuilder;

    public PartialSearchLocationBuilder(T parentTreeBuilder, SearchLocationBuilder searchLocationBuilder) {
        if (searchLocationBuilder == null) {
            throw new RuntimeException("Passed SearchLocationBuilder is null!");
        }
        this.searchLocationBuilder = searchLocationBuilder;
        this.parentTreeBuilder = parentTreeBuilder;
    }

    public PartialSearchLocationBuilder<T> filter(Predicate filter) {
        this.searchLocationBuilder.filter(filter);
        return this;
    }

    public T to(OnStartHandler onStartHandler) {
        this.searchLocationBuilder.onStartHandler(onStartHandler);
        return parentTreeBuilder;
    }
}
