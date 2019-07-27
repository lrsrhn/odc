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

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class SingleElementFinder implements ElementFinder {
  private String searchElement;
  private SearchLocation searchLocation;
  private ElementFinderReference thisReference;

  public SingleElementFinder() {
    this.searchLocation = new SearchLocation(false);
    this.thisReference = new ElementFinderReference(this);
  }

  @Override
  public ElementFinder setSearchElement(String searchElement, boolean isRelative) {
    if (this.searchElement == null) {
      this.searchElement = searchElement.intern();
      this.searchLocation.setRelative(isRelative);
      return thisReference;
    } else if (this.searchElement.equals(searchElement) && searchLocation.isRelative() == isRelative) {
      return thisReference;
    }
    MultipleArrayElementFinder multipleXmlElementFinder = new MultipleArrayElementFinder(thisReference, this.searchElement, searchLocation);
    return multipleXmlElementFinder.setSearchElement(searchElement, isRelative);
  }

  @Override
  public ElementFinder setPredicate(Predicate predicate) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
    if (this.searchElement == null) {
      this.searchElement = searchElement.intern();
      this.searchLocation.setRelative(isRelative);
      return new SearchLocationBuilder(this.searchLocation);
    } else if (this.searchElement.equals(searchElement) && searchLocation.isRelative() == isRelative) {
      return new SearchLocationBuilder(this.searchLocation);
    }
    MultipleArrayElementFinder multipleXmlElementFinder = new MultipleArrayElementFinder(thisReference, this.searchElement, searchLocation);
    return multipleXmlElementFinder.buildSearchLocation(searchElement, isRelative);
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
    if (searchElement == null) {
      toStringBuilder.append(previousElementsBuilder).append("/null\n");
      return;
    }
    previousElementsBuilder
            .append(searchLocation.isRelative() ? "//" : "/")
            .append(searchElement);
    PrettyPrintHelper.printSearchLocation(searchLocation, previousElementsBuilder, visited, toStringBuilder);
  }

  @Override
  public SearchLocation lookupSearchLocation(StructureElement structureElement, ObjectStore objectStore, boolean includeAbsolutes) {
    return includeAbsolutes | searchLocation.isRelative() ? lookupSearchLocation(structureElement, objectStore) : null;
  }

  @Override
  public SearchLocation lookupSearchLocation(StructureElement structureElement, ObjectStore objectStore) {
      if (searchElement.equals(structureElement.getElementName())) {
      return searchLocation;
    }
    return null;
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences() {
    if (searchLocation != null) {
      return Collections.singletonList(new SearchLocationReference(searchLocation, searchElement));
    }
    return Collections.emptyList();
  }

  @Override
  public void mergeElementFinder(ElementFinder elementFinder) {
    List<SearchLocationReference> searchLocationReferences = elementFinder.getSeachLocationReferences();
    if (searchLocationReferences.size() > 1) {
      MultipleArrayElementFinder multipleArrayElementFinder = new MultipleArrayElementFinder(thisReference, searchElement, searchLocation);
      multipleArrayElementFinder.mergeElementFinder(elementFinder);
    } else if (searchLocationReferences.size() ==  1) {
      SearchLocationReference searchLocationReference = searchLocationReferences.get(0);
      if (searchLocationReference.getPredicate() != null) {
        throw new IllegalStateException("Cannot do predicate");
      }
      if (searchLocation != null) {
        if (searchElement.equals(searchLocationReference.getSearchElement()) && searchLocation.isRelative() == searchLocationReference.isRelative()) {
          return;
        }
        MultipleArrayElementFinder multipleArrayElementFinder = new MultipleArrayElementFinder(thisReference, searchElement, searchLocation);
        multipleArrayElementFinder.mergeElementFinder(elementFinder);
      } else {
       searchLocation = searchLocationReference.getSearchLocation();
       searchElement = searchLocationReference.getSearchElement();
      }
    }
  }

  @Override
  public boolean isPredicate() {
    return false;
  }

  @Override
  public boolean hasRelative() {
    return searchLocation.isRelative();
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
