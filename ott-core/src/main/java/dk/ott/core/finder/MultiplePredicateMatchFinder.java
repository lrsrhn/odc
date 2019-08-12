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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MultiplePredicateMatchFinder implements ElementFinder {
  private SearchLocationList searchLocationList;
  private ElementFinderReference thisReference;

  public MultiplePredicateMatchFinder(ElementFinderReference elementFinderReference, Predicate predicate, SearchLocation searchLocation) {
    this.searchLocationList = new SearchLocationList();
    this.thisReference = elementFinderReference;
    this.thisReference.setElementFinder(this);
    if (predicate != null) {
      searchLocationList.addSearchLocation(searchLocation, predicate);
    }
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(Predicate predicate){
    int index = searchLocationList.findIndexByPredicate(predicate);
    if (index != -1) {
      return new SearchLocationBuilder(this, searchLocationList.searchLocations[index]);
    } else {
      SearchLocation searchLocation = new SearchLocation(false);
      searchLocationList.addSearchLocation(searchLocation, predicate);
      return new SearchLocationBuilder(this, searchLocation);
    }
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
    if (this.searchLocationList.getSize() == 0) {
      toStringBuilder.append(previousElementsBuilder).append("/null\n");
      return;
    }
    int previousElementBuilderLength = previousElementsBuilder.length();
    for (int i = 0; i < this.searchLocationList.getSize(); i++) {
      previousElementsBuilder.setLength(previousElementBuilderLength);
      previousElementsBuilder
              .append('[')
              .append(searchLocationList.predicates[i].toString())
              .append(']');
      PrettyPrintHelper.printSearchLocation(searchLocationList.searchLocations[i], previousElementsBuilder, visited, toStringBuilder);
    }
  }

  @Override
  public SearchLocation lookupSearchLocation(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
    for (int i = 0; i < searchLocationList.getSize(); i++) {
      if (searchLocationList.predicates[i].evaluate(elementCursor, objectStore)) {
        return searchLocationList.searchLocations[i];
      }
    }
    return null;
  }

  @Override
  public SearchLocation lookupSearchLocation(ElementCursor elementCursor, ObjectStore objectStore) {
    return lookupSearchLocation(elementCursor, objectStore, false);
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences() {
    List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(searchLocationList.getSize());
    for (int i = 0; i < searchLocationList.getSize(); i++) {
      references.add(new SearchLocationReference(searchLocationList.searchLocations[i], searchLocationList.predicates[i]));
    }
    return references;
  }

  @Override
  public void mergeElementFinder(ElementFinder elementFinder) {
    List<SearchLocationReference> searchLocationReferences = elementFinder.getSeachLocationReferences();
    for (SearchLocationReference searchLocationReference : searchLocationReferences) {
      if (searchLocationReference.getSearchElement() != null) {
        throw new RuntimeException("Unable to add reference using a searchElement!");
      }
      int predicateIndex = searchLocationList.findIndexByPredicate(searchLocationReference.getPredicate());
      if (predicateIndex != -1) {
        SearchLocation searchLocation = searchLocationList.searchLocations[predicateIndex];
        if (searchLocation.isRelative() == searchLocationReference.isRelative()) {
          searchLocation.merge(searchLocationReference.getSearchLocation());
        } else {
          searchLocationList.addSearchLocation(searchLocationReference.getSearchLocation(), searchLocationReference.getPredicate());
        }
      } else {
        searchLocationList.addSearchLocation(searchLocationReference.getSearchLocation(), searchLocationReference.getPredicate());
      }
    }
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
    for (int i = 0; i < this.searchLocationList.size; i++) {
      SearchLocation searchLocation = this.searchLocationList.searchLocations[i];
      if (searchLocation.getElementFinder() != null) {
        ElementFinder elementFinder = searchLocation.getElementFinder().getDereference();
        searchLocation.setElementFinder(elementFinder);
        elementFinder.unreferenceTree();
      }
    }
  }

  private final class SearchLocationList {
    SearchLocation[] searchLocations;
    Predicate[] predicates;
    private int size;

    public SearchLocationList() {
      this.searchLocations = new SearchLocation[4];
      this.predicates = new Predicate[4];
      this.size = 0;
    }

    public void addSearchLocation(SearchLocation searchLocation, Predicate predicate) {
      this.size++;
      searchLocations = addItemToList(searchLocation, searchLocations);
      predicates = addItemToList(predicate, predicates);
    }

    public int findIndexByPredicate(Predicate targetPredicate) {
      for (int i = 0; i < size; i++) {
        if (predicates[i].equals(targetPredicate)) {
          return i;
        }
      }
      return -1;
    }

    private <E> E[] addItemToList(E item, E[] items) {
      if (size > items.length) {
        items = Arrays.copyOf(items, items.length + 1);
      }
      items[size - 1] = item;
      return items;
    }

    public int getSize() {
      return size;
    }
  }
}