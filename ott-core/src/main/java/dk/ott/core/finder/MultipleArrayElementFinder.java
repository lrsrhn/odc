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

public final class MultipleArrayElementFinder implements ElementFinder {
  private SearcLocationList nextXmlElementFinders;
  private ElementFinderReference thisReference;
  private boolean hasRelatives;

  MultipleArrayElementFinder(ElementFinderReference thisReference, String searchElement, SearchLocation searchLocation) {
    this.thisReference = thisReference;
    thisReference.setElementFinder(this);
    this.nextXmlElementFinders = new SearcLocationList();
    if (searchElement != null) {
      hasRelatives = searchLocation.isRelative();
      this.nextXmlElementFinders.addSearchLocation(searchElement, searchLocation);
    }
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
    SearchLocation searchLocation = nextXmlElementFinders.lookupSearchLocation(searchElement, !isRelative);
    if (searchLocation == null) {
      searchLocation = new SearchLocation(null, null, null, isRelative);
      nextXmlElementFinders.addSearchLocation(searchElement, searchLocation);
      hasRelatives |= isRelative;
    }
    return new SearchLocationBuilder(this, searchLocation);
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
    for (int i = 0; i < nextXmlElementFinders.getSize(); i++) {
      previousElementsBuilder.setLength(previousElementBuilderLength);
      previousElementsBuilder
              .append(nextXmlElementFinders.searchLocations[i].isRelative() ? "//" : "/")
              .append(nextXmlElementFinders.elementNames[i]);
      PrettyPrintHelper.printSearchLocation(nextXmlElementFinders.searchLocations[i], previousElementsBuilder, visited, toStringBuilder);
    }
  }

  @Override
  public SearchLocation lookupSearchLocation(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
    return this.nextXmlElementFinders.lookupSearchLocation(elementCursor.getElementName(), includeAbsolutes);
  }

  @Override
  public SearchLocation lookupSearchLocation(ElementCursor elementCursor, ObjectStore objectStore) {
    return this.nextXmlElementFinders.lookupSearchLocation(elementCursor.getElementName(), true);
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences() {
    List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(nextXmlElementFinders.getSize());
    for (int i = 0; i < nextXmlElementFinders.getSize(); i++) {
      references.add(new SearchLocationReference(nextXmlElementFinders.searchLocations[i], nextXmlElementFinders.elementNames[i]));
    }
    return references;
  }

  @Override
  public void mergeElementFinder(ElementFinder elementFinder) {
    List<SearchLocationReference> searchLocationReferences = elementFinder.getSeachLocationReferences();
    for (SearchLocationReference searchLocationReference : searchLocationReferences) {
      if (searchLocationReference.getPredicate() != null) {
        throw new RuntimeException("Unable to add reference using a predicate!");
      }
      SearchLocation searchLocation = nextXmlElementFinders.lookupSearchLocation(searchLocationReference.getSearchElement(), !searchLocationReference.isRelative());
      if (searchLocation != null && searchLocation.isRelative() == searchLocationReference.isRelative()) {
        searchLocation.merge(searchLocationReference.getSearchLocation());
      } else {
        nextXmlElementFinders.addSearchLocation(searchLocationReference.getSearchElement(), searchLocationReference.getSearchLocation());
        hasRelatives |= searchLocationReference.isRelative();
      }
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
    for (int i = 0; i < nextXmlElementFinders.size; i++) {
      SearchLocation searchLocation = this.nextXmlElementFinders.searchLocations[i];
      if (searchLocation.getElementFinder() != null) {
        ElementFinder elementFinder = searchLocation.getElementFinder().getDereference();
        searchLocation.setElementFinder(elementFinder);
        elementFinder.unreferenceTree();
      }
    }
  }

  private final class SearcLocationList {
    SearchLocation[] searchLocations;
    String[] elementNames;
    int[] elementNameLengths;
    private int firstRelativeIndex; // Equal to count of absolutes
    private int size;

    SearcLocationList() {
      this.searchLocations = new SearchLocation[4];
      this.elementNames = new String[4];
      this.elementNameLengths = new int[4];
      this.size = 0;
      this.firstRelativeIndex = size;
    }

    final void addSearchLocation(String elementName, SearchLocation searchLocation) {
      this.size++;
      boolean isRelative = searchLocation.isRelative();
      searchLocations = addItemToList(searchLocation, searchLocations, isRelative);
      elementNames = addItemToList(elementName.intern(), elementNames, isRelative);
      elementNameLengths = addItemToList(elementName.length(), elementNameLengths, isRelative);
      if (!isRelative) {
        firstRelativeIndex++;
      }
    }

    final SearchLocation lookupSearchLocation(String targetElementName, boolean includeAbsolutes) {
      int targetElementNameLength = targetElementName.length();
      int firstIndex = includeAbsolutes ? 0 : firstRelativeIndex;
      for (int i = firstIndex; i < size; i++) {
        if (targetElementNameLength == elementNameLengths[i] && targetElementName.equals(elementNames[i])) {
          return nextXmlElementFinders.searchLocations[i];
        }
      }
      return null;
    }

    private <E> E[] addItemToList(E item, E[] items, boolean isRelative) {
      if (size > items.length) {
        items = Arrays.copyOf(items, items.length + 1);
      }
      if (isRelative) {
        items[size - 1] = item;
      } else {
        for (int i = size - 1; i > firstRelativeIndex; i--) {
          items[i] = items[i - 1];
        }
        items[firstRelativeIndex] = item;
      }
      return items;
    }

    private int[] addItemToList(int item, int[] items, boolean isRelative) {
      if (size > items.length) {
        items = Arrays.copyOf(items, items.length + 1);
      }
      if (isRelative) {
        items[size - 1] = item;
      } else {
        for (int i = size - 1; i > firstRelativeIndex; i--) {
          items[i] = items[i - 1];
        }
        items[firstRelativeIndex] = item;
      }
      return items;
    }

    final boolean isEmpty() {
      return size == 0;
    }

    public final int getSize() {
      return size;
    }
  }
}
