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
package dk.ott.core.finder;

import dk.ott.core.dsl.TreeEdgeReference;
import dk.ott.core.event.OnEndHandler;
import dk.ott.core.event.OnStartHandler;
import dk.ott.core.event.OnTextHandler;
import dk.ott.core.predicate.Predicate;

public class SearchLocationBuilder {
    private SearchLocation searchLocation;
    private ElementFinder elementFinder;

    public SearchLocationBuilder(ElementFinder elementFinder, SearchLocation searchLocation) {
        if (searchLocation == null) {
            throw new RuntimeException("Passed searchLocation is null!");
        }
        this.searchLocation = searchLocation;
        this.elementFinder = elementFinder;
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

    public SearchLocationBuilder textAsRaw() {
        searchLocation.getOrCreateTextLocation().setRaw(true);
        return this;
    }

    public SearchLocationBuilder onTextHandler(OnTextHandler onTextHandler) {
        this.searchLocation.getOrCreateTextLocation().setOnTextHandler(onTextHandler);
        return this;
    }

    public SearchLocationBuilder textFilter(Predicate textFilter) {
        this.searchLocation.getTextLocation().setTextFilter(textFilter);
        return this;
    }

    public SearchLocationBuilder addSearchElementFinder(String elementName, boolean isRelative) {
        return addSearchElementFinder().buildSearchLocation(elementName, isRelative);
    }

    public ElementFinder addSearchElementFinder() {
        ElementFinder elementFinder = searchLocation.getElementFinder();
        if (elementFinder == null) {
            elementFinder = new SingleElementFinder().getReference();
            this.searchLocation.setElementFinder(elementFinder);
        }
        return elementFinder;
    }

    public SearchLocationBuilder addPredicateElementFinder(Predicate predicate) {
        return addPredicateElementFinder().buildSearchLocation(predicate);
    }

    public ElementFinder addPredicateElementFinder() {
        ElementFinder elementFinder = searchLocation.getElementFinder();
        if (elementFinder == null) {
            elementFinder = new SinglePredicateMatchFinder().getReference();
            this.searchLocation.setElementFinder(elementFinder);
        }
        return elementFinder;
    }

    public SearchLocationBuilder addAllElementFinder() {
        ElementFinder elementFinder = searchLocation.getElementFinder();
        if (elementFinder == null) {
            elementFinder = new AllElementFinder();
            this.searchLocation.setElementFinder(elementFinder);
        }
        return elementFinder.buildSearchLocation(null);
    }

    public TreeEdgeReference toTreeEdgeReference() {
        return new TreeEdgeReference(elementFinder, searchLocation);
    }

    public SearchLocation build() {
        return searchLocation;
    }
}
