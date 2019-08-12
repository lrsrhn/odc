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
import dk.ott.core.processing.ElementCursor;

import java.util.List;
import java.util.Set;

public class ElementFinderReference implements ElementFinder {
    private ElementFinder elementFinder;

    public ElementFinderReference(ElementFinder elementFinder) {
        this.elementFinder = elementFinder;
    }

    public void setElementFinder(ElementFinder elementFinder) {
        this.elementFinder = elementFinder;
    }

    @Override
    public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
        return elementFinder.buildSearchLocation(searchElement, isRelative);
    }

    @Override
    public SearchLocationBuilder buildSearchLocation(Predicate predicate) {
        return elementFinder.buildSearchLocation(predicate);
    }

    @Override
    public ElementFinderReference getReference() {
        return this;
    }

    @Override
    public ElementFinder getDereference() {
        return elementFinder;
    }

    @Override
    public void buildToString(StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder) {
        elementFinder.buildToString(previousElementsBuilder, visited, toStringBuilder);
    }

    @Override
    public SearchLocation lookupSearchLocation(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
        return elementFinder.lookupSearchLocation(elementCursor, objectStore, includeAbsolutes);
    }

    @Override
    public SearchLocation lookupSearchLocation(ElementCursor elementCursor, ObjectStore objectStore) {
        return elementFinder.lookupSearchLocation(elementCursor, objectStore);
    }

    @Override
    public List<SearchLocationReference> getSeachLocationReferences() {
        return elementFinder.getSeachLocationReferences();
    }

    @Override
    public void mergeElementFinder(ElementFinder elementFinder) {
        this.elementFinder.mergeElementFinder(elementFinder);
    }

    @Override
    public boolean isPredicate() {
        return elementFinder.isPredicate();
    }

    @Override
    public boolean hasRelative() {
        return elementFinder.hasRelative();
    }

    @Override
    public void unreferenceTree() {
        elementFinder.unreferenceTree();
    }
}
