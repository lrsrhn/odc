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

import dk.ott.core.dsl.expression.SearchLocationReference;
import dk.ott.core.predicate.Predicate;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.StructureElement;

import java.util.List;
import java.util.Set;

public class AllElementFinder implements ElementFinder {
    private ElementFinderReference elementFinderReference;
    private SearchLocation searchLocation;

    public AllElementFinder() {
        this.searchLocation = new SearchLocation();
        this.elementFinderReference = new ElementFinderReference(this);
    }

    @Override
    public ElementFinder setSearchElement(String searchElement, boolean isRelative) {
        throw new UnsupportedOperationException("Not supported by All element finder");
    }

    @Override
    public ElementFinder setPredicate(Predicate predicate) {
        throw new UnsupportedOperationException("Not supported by All element finder");
    }

    @Override
    public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
        return new SearchLocationBuilder(searchLocation);
    }

    @Override
    public SearchLocationBuilder buildSearchLocation(Predicate predicate) {
        return new SearchLocationBuilder(searchLocation);
    }

    @Override
    public ElementFinderReference getReference() {
        return elementFinderReference;
    }

    @Override
    public ElementFinder getDereference() {
        return this;
    }

    @Override
    public SearchLocation lookupSearchLocation(StructureElement structureElement, ObjectStore objectStore, boolean isRelative) {
        return searchLocation;
    }

    @Override
    public List<SearchLocationReference> getSeachLocationReferences(boolean isRelative) {
        throw new UnsupportedOperationException("Not supported by All element finder");
    }

    @Override
    public void mergeElementFinder(ElementFinder elementFinder) {
        throw new UnsupportedOperationException("Not supported by All element finder");
    }

    @Override
    public void buildToString(StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder) {
        previousElementsBuilder.append("/*");
        PrettyPrintHelper.printSearchLocation(searchLocation, previousElementsBuilder, visited, toStringBuilder);
    }

    @Override
    public boolean isPredicate() {
        return false;
    }

    @Override
    public boolean hasRelative() {
        return true;
    }

    @Override
    public void unreferenceTree() {
    if (searchLocation.getElementFinder() != null) {
      ElementFinder elementFinder = searchLocation.getElementFinder().getDereference();
      searchLocation.setElementFinder(elementFinder);
      elementFinder.unreferenceTree();
    }
    }
}
