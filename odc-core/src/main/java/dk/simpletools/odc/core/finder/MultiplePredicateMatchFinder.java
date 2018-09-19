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

import dk.simpletools.odc.core.dsl.expression.SearchLocationReference;
import dk.simpletools.odc.core.processing.StructureElement;
import dk.simpletools.odc.core.predicate.Predicate;
import dk.simpletools.odc.core.processing.ElementFinderReference;

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
  public ElementFinder addNextElementFinder(String searchElement, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public ElementFinder addNextElementFinder(Predicate predicate, boolean isRelative) {
    int index = searchLocationList.findIndexByPredicate(predicate);
    if (index == -1) {
      ElementFinder newElementFinder = new SingleElementFinder().getReference();
      searchLocationList.addSearchLocation(new SearchLocation(newElementFinder, null, null), predicate);
      return newElementFinder;
    } else if (searchLocationList.searchLocations[index].getElementFinder() == null) {
      ElementFinder newElementFinder = new SingleElementFinder().getReference();
      searchLocationList.searchLocations[index].setElementFinder(newElementFinder);
      return newElementFinder;
    }
    return searchLocationList.searchLocations[index].getElementFinder();
  }

  @Override
  public ElementFinder addNextPredicate(String searchElement) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public ElementFinder setSearchElement(String searchElement, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public ElementFinder setPredicate(Predicate predicate) {
    int index = searchLocationList.findIndexByPredicate(predicate);
    if (index == -1) {
      searchLocationList.addSearchLocation(new SearchLocation(), predicate);
    }
    return this;
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(Predicate predicate){
    int index = searchLocationList.findIndexByPredicate(predicate);
    if (index != -1) {
      return new SearchLocationBuilder(searchLocationList.searchLocations[index]);
    } else {
      SearchLocation searchLocation = new SearchLocation();
      searchLocationList.addSearchLocation(searchLocation, predicate);
      return new SearchLocationBuilder(searchLocation);
    }
  }

  @Override
  public ElementFinderReference getReference() {
    return thisReference;
  }

  @Override
  public SearchLocation lookupSearchLocation(String elementName, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public SearchLocation lookupSearchLocation(Predicate predicate){
    for (int i = 0; i < searchLocationList.getSize(); i++) {
      if (predicate.equals(searchLocationList.predicates[i])) {
        return searchLocationList.searchLocations[i];
      }
    }
    return null;
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
  public SearchLocation lookupSearchLocation(StructureElement structureElement, boolean isRelative) {
    if (!isRelative) {
        for (int i = 0; i < searchLocationList.getSize(); i++) {
          if (searchLocationList.predicates[i].evaluate(structureElement)) {
            return searchLocationList.searchLocations[i];
          }
        }
    }
    return null;
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences(boolean isRelative) {
    List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(searchLocationList.getSize());
    for (int i = 0; i < searchLocationList.getSize(); i++) {
      references.add(new SearchLocationReference(searchLocationList.searchLocations[i], searchLocationList.predicates[i], false));
    }
    return references;
  }

  @Override
  public void mergeElementFinder(ElementFinder elementFinder) {
    List<SearchLocationReference> searchLocationReferences = elementFinder.getSeachLocationReferences(false);
    for (SearchLocationReference searchLocationReference : searchLocationReferences) {
      if (searchLocationReference.getSearchElement() != null) {
        throw new RuntimeException("Unable to add reference using a searchElement!");
      }
      if (searchLocationList.findIndexByPredicate(searchLocationReference.getPredicate()) != -1) {
        throw new RuntimeException("A searchElement already exists: " + searchLocationReference.getSearchElement());
      }
      searchLocationList.addSearchLocation(searchLocationReference.getSearchLocation(), searchLocationReference.getPredicate());
    }
  }

  @Override
  public boolean isPredicate() {
    return true;
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