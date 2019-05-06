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

public class SinglePredicateMatchFinder implements ElementFinder {
  private Predicate predicate;
  private SearchLocation searchLocation;
  private ElementFinderReference thisReference;

  public SinglePredicateMatchFinder() {
    this.searchLocation = new SearchLocation(false);
    this.thisReference = new ElementFinderReference(this);
  }

  @Override
  public ElementFinder setSearchElement(String searchElement, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public ElementFinder setPredicate(Predicate predicate) {
    if (this.predicate == null) {
      this.predicate = predicate;
    } else if (!this.predicate.equals(predicate)) {
      MultiplePredicateMatchFinder multiplePredicateMatchFinder = new MultiplePredicateMatchFinder(thisReference, this.predicate, searchLocation);
      thisReference.setElementFinder(multiplePredicateMatchFinder);
      return multiplePredicateMatchFinder.setPredicate(predicate);
    }
    return thisReference;
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(Predicate predicate) {
    if (this.predicate == null) {
      this.predicate = predicate;
      this.searchLocation.setRelative(false);
      return new SearchLocationBuilder(this.searchLocation);
    } else if (this.predicate.equals(predicate)) {
      return new SearchLocationBuilder(this.searchLocation);
    }
    MultiplePredicateMatchFinder multiplePredicateMatchFinder = new MultiplePredicateMatchFinder(thisReference, this.predicate, searchLocation);
    return multiplePredicateMatchFinder.buildSearchLocation(predicate);
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
    if (this.searchLocation == null || this.predicate == null) {
      toStringBuilder.append(previousElementsBuilder).append("/null\n");
      return;
    }
    previousElementsBuilder.append('[').append(predicate.toString()).append(']');
    PrettyPrintHelper.printSearchLocation(searchLocation, previousElementsBuilder, visited, toStringBuilder);
  }

  @Override
  public SearchLocation lookupSearchLocation(StructureElement structureElement, ObjectStore objectStore, boolean includeAbsolutes) {
    return lookupSearchLocation(structureElement, objectStore);
  }

  @Override
  public SearchLocation lookupSearchLocation(StructureElement structureElement, ObjectStore objectStore) {
    if (predicate.evaluate(structureElement, objectStore)) {
      return searchLocation;
    }
    return null;
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences(boolean isRelative) {
    if (predicate != null) {
      return Collections.singletonList(new SearchLocationReference(searchLocation, predicate));
    }
    return Collections.emptyList();
  }

  @Override
  public void mergeElementFinder(ElementFinder elementFinder) {
    List<SearchLocationReference> searchLocationReferences = elementFinder.getSeachLocationReferences(searchLocation.isRelative());
    if (searchLocationReferences.size() > 1) {
      MultiplePredicateMatchFinder multiplePredicateMatchFinder = new MultiplePredicateMatchFinder(thisReference, predicate, searchLocation);
      multiplePredicateMatchFinder.mergeElementFinder(elementFinder);
    } else if (searchLocationReferences.size() ==  1) {
      SearchLocationReference searchLocationReference = searchLocationReferences.get(0);
      if (searchLocationReference.getSearchElement() != null) {
        throw new IllegalStateException("Cannot do search elements");
      }
      if (searchLocation != null) {
        if (predicate.equals(searchLocationReference.getPredicate()) && searchLocation.isRelative() == searchLocationReference.isRelative()) {
          return;
        }
        MultiplePredicateMatchFinder multiplePredicateMatchFinder = new MultiplePredicateMatchFinder(thisReference, predicate, searchLocation);
        multiplePredicateMatchFinder.mergeElementFinder(elementFinder);
      } else {
       searchLocation = searchLocationReference.getSearchLocation();
       predicate = searchLocationReference.getPredicate();
      }
    }

    MultiplePredicateMatchFinder multiplePredicateMatchFinder = new MultiplePredicateMatchFinder(getReference(), predicate, searchLocation);
    multiplePredicateMatchFinder.mergeElementFinder(elementFinder);
  }

  @Override
  public boolean isPredicate() {
    return true;
  }

  @Override
  public boolean hasRelative() {
    return false;
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
