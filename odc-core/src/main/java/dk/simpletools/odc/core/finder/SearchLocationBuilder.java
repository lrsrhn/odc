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
package dk.simpletools.odc.core.finder;

import dk.simpletools.odc.core.predicate.Predicate;

public class SearchLocationBuilder {
    private SearchLocation searchLocation;

    public SearchLocationBuilder(SearchLocation searchLocation) {
        if (searchLocation == null) {
            throw new RuntimeException("Passed searchLocation is null!");
        }
        this.searchLocation = searchLocation;
    }

    public SearchLocationBuilder filter(Predicate filter) {
        this.searchLocation.setFilter(filter);
        return this;
    }

    public SearchLocationBuilder onStartHandler(OnStartHandler onStartHandler) {
        this.searchLocation.setOnStartHandler(onStartHandler);
        return this;
    }

    public SearchLocationBuilder onEndHandler(OnEndHandler onEndHandler) {
        this.searchLocation.setOnEndHandler(onEndHandler);
        return this;
    }

    public ElementFinder addSearchElementFinder() {
        if (searchLocation.getElementFinder() == null) {
            this.searchLocation.setElementFinder(new SingleElementFinder().getReference());
        }
        return searchLocation.getElementFinder();
    }

    public ElementFinder addPredicateElementFinder() {
        if (searchLocation.getElementFinder() == null) {
            this.searchLocation.setElementFinder(new SinglePredicateMatchFinder().getReference());
        }
        return searchLocation.getElementFinder();
    }

    public ElementFinder addAllElementFinder() {
        if (searchLocation.getElementFinder() == null) {
            this.searchLocation.setElementFinder(new AllElementFinder());
        }
        return searchLocation.getElementFinder();
    }

    public SearchLocation build() {
        return searchLocation;
    }
}
