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

import java.util.*;

public class MultipleElementFinder implements ElementFinder {
  private HashMap<String, SearchLocation> nextXmlElementFinders;
  private ElementFinderReference thisReference;
  private boolean hasRelatives;

  MultipleElementFinder(ElementFinderReference thisReference, String searchElement, SearchLocation searchLocation) {
    this.thisReference = thisReference;
    thisReference.setElementFinder(this);
    this.nextXmlElementFinders = new HashMap<String, SearchLocation>(4);
    hasRelatives = searchLocation.isRelative();
    this.nextXmlElementFinders.put(searchElement.intern(), searchLocation);
  }

  @Override
  public ElementFinder setSearchElement(String searchElement, boolean isRelative) {
    SearchLocation searchLocation = nextXmlElementFinders.get(searchElement);
    if (searchLocation == null || searchLocation.isRelative() != isRelative) {
      hasRelatives |= isRelative;
      nextXmlElementFinders.put(searchElement.intern(), new SearchLocation(null, null, null, isRelative));
    }
    return this;
  }

  @Override
  public ElementFinder setPredicate(Predicate predicate) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
    SearchLocation searchLocation = nextXmlElementFinders.get(searchElement);
    if (searchLocation == null || searchLocation.isRelative() != isRelative) {
      searchLocation = new SearchLocation(null, null, null, isRelative);
      nextXmlElementFinders.put(searchElement.intern(), searchLocation);
      hasRelatives |= isRelative;
    }
    return new SearchLocationBuilder(searchLocation);
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(Predicate predicate) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public ElementFinderReference getReference() {
    return thisReference;
  }

  @Override
  public ElementFinder getDereference() {
    return this;
  }

  @Override
  public void buildToString(StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder) {
    if (nextXmlElementFinders.isEmpty()) {
      toStringBuilder.append(previousElementsBuilder).append("/null\n");
      return;
    }
    int previousElementBuilderLength = previousElementsBuilder.length();
    for (Map.Entry<String, SearchLocation> entries : nextXmlElementFinders.entrySet()) {
      previousElementsBuilder.setLength(previousElementBuilderLength);
      previousElementsBuilder
              .append(entries.getValue().isRelative() ? "//" : "/")
              .append(entries.getKey());
      PrettyPrintHelper.printSearchLocation(entries.getValue(), previousElementsBuilder, visited, toStringBuilder);
    }
  }

  @Override
  public SearchLocation lookupSearchLocation(StructureElement structureElement, ObjectStore objectStore, boolean includeAbsolutes) {
    String targetElementName = structureElement.getElementName();
    SearchLocation searchLocation = this.nextXmlElementFinders.get(targetElementName);
    if (searchLocation != null && searchLocation.isRelative() | includeAbsolutes) {
      return searchLocation;
    }
    return null;
  }

  @Override
  public SearchLocation lookupSearchLocation(StructureElement structureElement, ObjectStore objectStore) {
    return lookupSearchLocation(structureElement, objectStore, true);
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences(boolean isRelative) {
    List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(nextXmlElementFinders.size());
    for (Map.Entry<String, SearchLocation> entry : nextXmlElementFinders.entrySet()) {
      if (entry.getValue().isRelative() == isRelative) {
        references.add(new SearchLocationReference(entry.getValue(), entry.getKey()));
      }
    }
    return references;
  }

  @Override
  public void mergeElementFinder(ElementFinder elementFinder) {
    mergeElementFinder(elementFinder, false);
    mergeElementFinder(elementFinder, true);
  }

  private void mergeElementFinder(ElementFinder elementFinder, boolean isRelative) {
    List<SearchLocationReference> searchLocationReferences = elementFinder.getSeachLocationReferences(isRelative);
    for (SearchLocationReference searchLocationReference : searchLocationReferences) {
      if (searchLocationReference.getPredicate() != null) {
        throw new RuntimeException("Unable to add reference using a predicate!");
      }
      SearchLocation searchLocation = nextXmlElementFinders.get(searchLocationReference.getSearchElement());
      if (searchLocation != null && searchLocation.isRelative() == isRelative) {
        throw new RuntimeException("A searchElement already exists: " + searchLocationReference.getSearchElement());
      }
      hasRelatives |= isRelative;
      nextXmlElementFinders.put(searchLocationReference.getSearchElement(), searchLocationReference.getSearchLocation());
    }
  }

  @Override
  public boolean isPredicate() {
    return false;
  }

  @Override
  public boolean hasRelative() {
    return hasRelatives;
  }

  @Override
  public void unreferenceTree() {
    for (SearchLocation searchLocation : nextXmlElementFinders.values()) {
      if (searchLocation.getElementFinder() != null) {
        ElementFinder elementFinder = searchLocation.getElementFinder().getDereference();
        searchLocation.setElementFinder(elementFinder);
        elementFinder.unreferenceTree();
      }
    }
    for (SearchLocation searchLocation : nextXmlElementFinders.values()) {
      if (searchLocation.getElementFinder() != null) {
        ElementFinder elementFinder = searchLocation.getElementFinder().getDereference();
        searchLocation.setElementFinder(elementFinder);
        elementFinder.unreferenceTree();
      }
    }
  }

  private static void buildToStringForMap(boolean isRelative, StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder, Map<String, SearchLocation> elementFinders) {
    int previousElementBuilderLength = previousElementsBuilder.length();
    for (Map.Entry<String, SearchLocation> entries : elementFinders.entrySet()) {
      previousElementsBuilder.setLength(previousElementBuilderLength);
      previousElementsBuilder
              .append(isRelative ? "//" : "/")
              .append(entries.getKey());
      PrettyPrintHelper.printSearchLocation(entries.getValue(), previousElementsBuilder, visited, toStringBuilder);
    }
  }
}
