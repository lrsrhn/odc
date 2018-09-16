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
import dk.simpletools.odc.core.predicate.Predicate;
import dk.simpletools.odc.core.processing.ElementFinderReference;
import dk.simpletools.odc.core.processing.StructureElement;

import java.util.*;

public final class MultipleArrayElementFinder implements ElementFinder {
  private SearcLocationList nextXmlElementFinders;
  private ElementFinderReference thisReference;
  private SearcLocationList relativeElementFinders;

  MultipleArrayElementFinder(ElementFinderReference thisReference, boolean isRelative, String searchElement, SearchLocation searchLocation) {
    this.thisReference = thisReference;
    thisReference.setElementFinder(this);
    this.nextXmlElementFinders = new SearcLocationList();
    this.relativeElementFinders = new SearcLocationList();
    if (searchElement != null) {
      if (isRelative) {
        this.relativeElementFinders.addSearchLocation(searchElement, searchLocation);
      } else {
        this.nextXmlElementFinders.addSearchLocation(searchElement, searchLocation);
      }
    }
  }

  @Override
  public ElementFinder addNextElementFinder(String searchElement, boolean isRelative) {
    SearcLocationList searcLocationList = selectElementFinderListByRelativity(isRelative);
    SearchLocation searchLocation = searcLocationList.lookupSearchLocation(searchElement);
    if (searchLocation == null) {
      ElementFinder elementFinder = new SingleElementFinder().getReference();
      nextXmlElementFinders.addSearchLocation(searchElement, new SearchLocation(elementFinder, null, null));
      return elementFinder;
    } else if (searchLocation.getElementFinder() == null) {
      ElementFinder elementFinder = new SingleElementFinder().getReference();
      searchLocation.setElementFinder(elementFinder);
    }
    return searchLocation.getElementFinder();
  }

  /**
   * Expects that searchElement is in absolute or relative. Not in both.
   */
  @Override
  public ElementFinder addNextPredicate(String searchElement) {
    SearchLocation searchLocation = nextXmlElementFinders.lookupSearchLocation(searchElement);
    if (searchLocation != null) {
      if (searchLocation.getElementFinder() == null) {
        ElementFinder elementFinder = new SinglePredicateMatchFinder().getReference();
        searchLocation.setElementFinder(elementFinder);
        return elementFinder;
      }
      return searchLocation.getElementFinder();
    }
    searchLocation = relativeElementFinders.lookupSearchLocation(searchElement);
    if (searchLocation != null) {
      if (searchLocation.getElementFinder() == null) {
        ElementFinder elementFinder = new SinglePredicateMatchFinder().getReference();
        searchLocation.setElementFinder(elementFinder);
        return elementFinder;
      }
      return searchLocation.getElementFinder();
    }
    throw new IllegalArgumentException("Did not find matching search element");
  }

  @Override
  public ElementFinder addNextElementFinder(Predicate predicate, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public ElementFinder setSearchElement(String searchElement, boolean isRelative) {
    SearcLocationList searcLocationList = selectElementFinderListByRelativity(isRelative);
    SearchLocation searchLocation = searcLocationList.lookupSearchLocation(searchElement);
    if (searchLocation == null) {
      nextXmlElementFinders.addSearchLocation(searchElement, new SearchLocation(null, null, null));
    }
    return this;
  }

  @Override
  public ElementFinder setPredicate(Predicate predicate) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
    SearcLocationList searcLocationList = selectElementFinderListByRelativity(isRelative);
    SearchLocation searchLocation = searcLocationList.lookupSearchLocation(searchElement);
    if (searchLocation == null) {
      searchLocation = new SearchLocation(null, null, null);
      nextXmlElementFinders.addSearchLocation(searchElement, searchLocation);
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
  public SearchLocation lookupSearchLocation(String elementName, boolean isRelative) {
    SearcLocationList searcLocationList = selectElementFinderListByRelativity(isRelative);
    return searcLocationList.isEmpty() ? null : searcLocationList.lookupSearchLocation(elementName);
  }

  @Override
  public SearchLocation lookupSearchLocation(Predicate predicate) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public void buildToString(StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder) {
    if (nextXmlElementFinders.isEmpty() && relativeElementFinders.isEmpty()) {
      toStringBuilder.append(previousElementsBuilder).append("/null\n");
      return;
    }
    buildToStringForMap(false, previousElementsBuilder, visited, toStringBuilder, nextXmlElementFinders);
    buildToStringForMap(true, previousElementsBuilder, visited, toStringBuilder, relativeElementFinders);
  }

  @Override
  public SearchLocation lookupSearchLocation(StructureElement structureElement, boolean isRelative) {
    SearcLocationList searcLocationList = selectElementFinderListByRelativity(isRelative);
    return searcLocationList.lookupSearchLocation(structureElement.getElementName());
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences(boolean isRelative) {
    SearcLocationList searcLocationList = selectElementFinderListByRelativity(isRelative);
    List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(searcLocationList.getSize());
    for (int i = 0; i < searcLocationList.getSize(); i++) {
      references.add(new SearchLocationReference(searcLocationList.searchLocations[i], searcLocationList.elementNames[i], isRelative));
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
    SearcLocationList searcLocationList = selectElementFinderListByRelativity(isRelative);
    for (SearchLocationReference searchLocationReference : searchLocationReferences) {
      if (searchLocationReference.getPredicate() != null) {
        throw new RuntimeException("Unable to add reference using a predicate!");
      }
      if (searcLocationList.lookupSearchLocation(searchLocationReference.getSearchElement()) != null) {
        throw new RuntimeException("A searchElement already exists: " + searchLocationReference.getSearchElement());
      }
      searcLocationList.addSearchLocation(searchLocationReference.getSearchElement(), searchLocationReference.getSearchLocation());
    }
  }

  @Override
  public boolean isPredicate() {
    return false;
  }

  private static void buildToStringForMap(boolean isRelative, StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder, SearcLocationList elementFinders) {
    int previousElementBuilderLength = previousElementsBuilder.length();
    for (int i = 0; i < elementFinders.getSize(); i++) {
      previousElementsBuilder.setLength(previousElementBuilderLength);
      previousElementsBuilder
              .append(isRelative ? "//" : "/")
              .append(elementFinders.elementNames[i]);
      SearchLocation searchLocation = elementFinders.searchLocations[i];
      if (searchLocation.getOnStartHandler() != null) {
        toStringBuilder
            .append(previousElementsBuilder)
            .append(" => ")
            .append(searchLocation.getOnStartHandler().getClass().getName())
            .append('\n');
      }
      if (searchLocation.getElementFinder() != null) {
        if (!visited.contains(searchLocation.getElementFinder())) {
          visited.add(searchLocation.getElementFinder());
          searchLocation.getElementFinder().buildToString(previousElementsBuilder, visited, toStringBuilder);
          visited.remove(searchLocation.getElementFinder());
        } else {
          toStringBuilder
                  .append(previousElementsBuilder)
                  .append(" <=>\n");
        }
      }
    }
  }

  private SearcLocationList selectElementFinderListByRelativity(boolean isRelative) {
    return isRelative ?  relativeElementFinders : nextXmlElementFinders;
  }

  private final class SearcLocationList {
    SearchLocation[] searchLocations;
    String[] elementNames;
    private int size;

    SearcLocationList() {
      this.searchLocations = new SearchLocation[4];
      this.elementNames = new String[4];
      this.size = 0;
    }

    final void addSearchLocation(String elementName, SearchLocation searchLocation) {
      this.size++;
      searchLocations = addItemToList(searchLocation, searchLocations);
      elementNames = addItemToList(elementName.intern(), elementNames);
    }

    final SearchLocation lookupSearchLocation(String targetElementName) {
      for (int i = 0; i < size; i++) {
        if (elementNames[i].equals(targetElementName)) {
          return searchLocations[i];
        }
      }
      return null;
    }

    private <E> E[] addItemToList(E item, E[] items) {
      if (size > items.length) {
        items = Arrays.copyOf(items, items.length + 1);
      }
      items[size - 1] = item;
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
