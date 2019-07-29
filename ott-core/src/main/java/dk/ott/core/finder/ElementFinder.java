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

public interface ElementFinder {

  SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative);

  SearchLocationBuilder buildSearchLocation(Predicate predicate);

  ElementFinderReference getReference();

  ElementFinder getDereference();

  SearchLocation lookupSearchLocation(StructureElement structureElement, ObjectStore objectStore, boolean includeAbsolutes);

  SearchLocation lookupSearchLocation(StructureElement structureElement, ObjectStore objectStore);

  List<SearchLocationReference> getSeachLocationReferences();

  void mergeElementFinder(ElementFinder elementFinder);

  void buildToString(StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder);

  boolean isPredicate();

  boolean hasRelative();

  void unreferenceTree();
}
