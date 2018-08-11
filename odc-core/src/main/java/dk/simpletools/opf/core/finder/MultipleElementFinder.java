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
package dk.simpletools.opf.core.finder;

import dk.simpletools.opf.core.dsl.expression.SearchLocationReference;
import dk.simpletools.opf.core.predicate.Predicate;
import dk.simpletools.opf.core.processing.ElementFinderReference;
import dk.simpletools.opf.core.processing.StructureElement;

import java.util.*;

public class MultipleElementFinder implements ElementFinder {
  private HashMap<String, SearchLocation> nextXmlElementFinders;
  private ElementFinderReference thisReference;
  private HashMap<String, SearchLocation> relativeElementFinders;

  MultipleElementFinder(ElementFinderReference thisReference, boolean isRelative, String searchElement, SearchLocation searchLocation) {
    this.thisReference = thisReference;
    thisReference.setElementFinder(this);
    this.nextXmlElementFinders = new HashMap<String, SearchLocation>(4);
    this.relativeElementFinders = new HashMap<String, SearchLocation>(2);
    if (isRelative) {
      this.relativeElementFinders.put(searchElement.intern(), searchLocation);
    } else {
      this.nextXmlElementFinders.put(searchElement.intern(), searchLocation);
    }
  }

  @Override
  public ElementFinder addNextElementFinder(String searchElement, boolean isRelative) {
    Map<String, SearchLocation> elementFinderMap = isRelative ?  relativeElementFinders : nextXmlElementFinders;
    SearchLocation searchLocation = elementFinderMap.get(searchElement);
    if (searchLocation == null) {
      ElementFinder elementFinder = new SingleElementFinder().getReference();
      elementFinderMap.put(searchElement.intern(), new SearchLocation(elementFinder, null, null));
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
    SearchLocation searchLocation = nextXmlElementFinders.get(searchElement);
    if (searchLocation != null) {
      if (searchLocation.getElementFinder() == null) {
        ElementFinder elementFinder = new SinglePredicateMatchFinder().getReference();
        searchLocation.setElementFinder(elementFinder);
        return elementFinder;
      }
      return searchLocation.getElementFinder();
    }
    searchLocation = relativeElementFinders.get(searchElement);
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
    Map<String, SearchLocation> elementFinderMap = isRelative ?  relativeElementFinders : nextXmlElementFinders;
    SearchLocation searchLocation = elementFinderMap.get(searchElement);
    if (searchLocation == null) {
      elementFinderMap.put(searchElement.intern(), new SearchLocation(null, null, null));
    }
    return this;
  }

  @Override
  public ElementFinder setPredicate(Predicate predicate) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
    Map<String, SearchLocation> elementFinderMap = isRelative ?  relativeElementFinders : nextXmlElementFinders;
    SearchLocation searchLocation = elementFinderMap.get(searchElement);
    if (searchLocation == null) {
      searchLocation = new SearchLocation(null, null, null);
      elementFinderMap.put(searchElement.intern(), searchLocation);
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
    Map<String, SearchLocation> elementFinderMap = isRelative ?  relativeElementFinders : nextXmlElementFinders;
    if (elementFinderMap.isEmpty()) {
      return null;
    }
    return elementFinderMap.get(elementName);
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
    Map<String, SearchLocation> elementFinder = isRelative ? relativeElementFinders : nextXmlElementFinders;
    return elementFinder.isEmpty() ? null : elementFinder.get(structureElement.getElementName());
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences(boolean isRelative) {
    Map<String, SearchLocation> elementFinders = isRelative ?  relativeElementFinders : nextXmlElementFinders;
    List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(elementFinders.size());
    for (Map.Entry<String, SearchLocation> entry : elementFinders.entrySet()) {
      references.add(new SearchLocationReference(entry.getValue(), entry.getKey(), isRelative));
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
    Map<String, SearchLocation> elementFinders = isRelative ?  relativeElementFinders : nextXmlElementFinders;
    for (SearchLocationReference searchLocationReference : searchLocationReferences) {
      if (searchLocationReference.getPredicate() != null) {
        throw new RuntimeException("Unable to add reference using a predicate!");
      }
      if (elementFinders.containsKey(searchLocationReference.getSearchElement())) {
        throw new RuntimeException("A searchElement already exists: " + searchLocationReference.getSearchElement());
      }
      elementFinders.put(searchLocationReference.getSearchElement(), searchLocationReference.getSearchLocation());
    }
  }

  @Override
  public boolean isPredicate() {
    return false;
  }

  private static void buildToStringForMap(boolean isRelative, StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder, Map<String, SearchLocation> elementFinders) {
    int previousElementBuilderLength = previousElementsBuilder.length();
    for (Map.Entry<String, SearchLocation> entries : elementFinders.entrySet()) {
      previousElementsBuilder.setLength(previousElementBuilderLength);
      previousElementsBuilder
              .append(isRelative ? "//" : "/")
              .append(entries.getKey());
      if (entries.getValue().getOnStartHandler() != null) {
        toStringBuilder
            .append(previousElementsBuilder)
            .append(" => ")
            .append(entries.getValue().getOnStartHandler().getClass().getName())
            .append('\n');
      }
      if (entries.getValue().getElementFinder() != null) {
        if (!visited.contains(entries.getValue().getElementFinder())) {
          visited.add(entries.getValue().getElementFinder());
          entries.getValue().getElementFinder().buildToString(previousElementsBuilder, visited, toStringBuilder);
          visited.remove(entries.getValue().getElementFinder());
        } else {
          toStringBuilder
                  .append(previousElementsBuilder)
                  .append(" <=>\n");
        }
      }
    }
  }
}
