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
package dk.ott.core.dsl.expression;

import dk.ott.core.finder.SearchLocation;
import dk.ott.core.predicate.Predicate;

public class SearchLocationReference {
    private String searchElement;
    private Predicate predicate;
    private SearchLocation searchLocation;

    public SearchLocationReference(SearchLocation searchLocation, String searchElement) {
        this.searchLocation = searchLocation;
        this.searchElement = searchElement;
    }

    public SearchLocationReference(SearchLocation searchLocation, Predicate predicate) {
        this.searchLocation = searchLocation;
        this.predicate = predicate;
    }

    public String getSearchElement() {
        return searchElement;
    }

    public void setSearchElement(String searchElement) {
        this.searchElement = searchElement;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public SearchLocation getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(SearchLocation searchLocation) {
        this.searchLocation = searchLocation;
    }

    public boolean isRelative() {
        return searchLocation.isRelative();
    }
}
