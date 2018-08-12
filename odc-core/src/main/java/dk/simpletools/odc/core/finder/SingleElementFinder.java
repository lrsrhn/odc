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

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class SingleElementFinder implements ElementFinder {
  private String searchElement;
  private boolean isRelative;
  private SearchLocation searchLocation;
  private ElementFinderReference thisReference;

  public SingleElementFinder() {
    this.searchLocation = new SearchLocation();
    this.thisReference = new ElementFinderReference(this);
  }

  @Override
  public ElementFinder addNextElementFinder(String searchElement, boolean isRelative) {
    if (this.searchElement == null) {
      this.searchElement = searchElement.intern();
      this.isRelative = isRelative;
      searchLocation.setElementFinder(new SingleElementFinder().getReference());
      return searchLocation.getElementFinder();
    } else {
      searchElement = searchElement.intern();
      if (this.searchElement.equals(searchElement)) {
        if (this.isRelative != isRelative) {
          MultipleElementFinder multipleXmlElementFinder = new MultipleElementFinder(thisReference, this.isRelative, this.searchElement, searchLocation);
          return multipleXmlElementFinder.addNextElementFinder(searchElement, isRelative);
        } else if (searchLocation.getElementFinder() == null) {
          searchLocation.setElementFinder(new SingleElementFinder().getReference());
          return searchLocation.getElementFinder();
        }
      } else {
        MultipleElementFinder multipleXmlElementFinder = new MultipleElementFinder(thisReference, this.isRelative, this.searchElement, searchLocation);
        return multipleXmlElementFinder.addNextElementFinder(searchElement, isRelative);
      }
    }
    return searchLocation.getElementFinder();
  }

  @Override
  public ElementFinder addNextElementFinder(Predicate predicate, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public ElementFinder addNextPredicate(String searchElement) {
    if (this.searchElement == null) {
      this.searchElement = searchElement.intern();
      searchLocation.setElementFinder(new SinglePredicateMatchFinder().getReference());
      return searchLocation.getElementFinder();
    } else if (this.searchElement.equalsIgnoreCase(searchElement)) {
      if (searchLocation.getElementFinder() == null) {
        searchLocation.setElementFinder(new SinglePredicateMatchFinder().getReference());
      }
      return searchLocation.getElementFinder();
    }
    throw new IllegalArgumentException(String.format("SearchElement mismatch: %s != %s", this.searchElement, searchElement));
  }

  @Override
  public ElementFinder setSearchElement(String searchElement, boolean isRelative) {
    if (this.searchElement == null) {
      this.searchElement = searchElement.intern();
      this.isRelative = isRelative;
    } else {
      searchElement = searchElement.intern();
      if (this.searchElement.equals(searchElement)) {
        if (this.isRelative != isRelative) {
          MultipleElementFinder multipleXmlElementFinder = new MultipleElementFinder(thisReference, this.isRelative, this.searchElement, searchLocation);
          return multipleXmlElementFinder.setSearchElement(searchElement, isRelative);
        }
      } else {
        MultipleElementFinder multipleXmlElementFinder = new MultipleElementFinder(thisReference, this.isRelative, this.searchElement, searchLocation);
        return multipleXmlElementFinder.setSearchElement(searchElement, isRelative);
      }
    }
    return thisReference;
  }

  @Override
  public ElementFinder setPredicate(Predicate predicate) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
    if (this.isRelative == isRelative) {
      if (this.searchElement == null) {
        this.searchElement = searchElement.intern();
        this.searchLocation = new SearchLocation();
        return new SearchLocationBuilder(this.searchLocation);
      } else if (this.searchElement.equals(searchElement)) {
        if (this.searchLocation == null) {
          this.searchLocation = new SearchLocation();
        }
        return new SearchLocationBuilder(this.searchLocation);
      } else {
        MultipleElementFinder multipleXmlElementFinder = new MultipleElementFinder(thisReference, this.isRelative, this.searchElement, searchLocation);
        return multipleXmlElementFinder.buildSearchLocation(searchElement, isRelative);
      }
    } else {
      MultipleElementFinder multipleXmlElementFinder = new MultipleElementFinder(thisReference, this.isRelative, this.searchElement, searchLocation);
      return multipleXmlElementFinder.buildSearchLocation(searchElement, isRelative);
    }
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
    if (this.isRelative == isRelative && elementName.equals(searchElement)) {
      return searchLocation;
    }
    return null;
  }

  @Override
  public SearchLocation lookupSearchLocation(Predicate predicate) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public void buildToString(StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder) {
//    if (elementHandler == null && nextElementFinder == null) {
//      toStringBuilder.append(previousElementsBuilder).append("/null\n");
//      return;
//    }
//    previousElementsBuilder
//            .append(isRelative ? "//" : "/")
//            .append(searchElement);
//    if (elementHandler != null) {
//      toStringBuilder
//          .append(previousElementsBuilder)
//          .append(" => ")
//          .append(elementHandler.getClass().getName())
//          .append('\n');
//    }
//    if (nextElementFinder != null) {
//      if (!visited.contains(nextElementFinder)) {
//        visited.add(nextElementFinder);
//        nextElementFinder.buildToString(previousElementsBuilder, visited, toStringBuilder);
//        visited.remove(nextElementFinder);
//      } else {
//        toStringBuilder
//                .append(previousElementsBuilder)
//                .append(" <=>\n");
//      }
//    }
  }

  @Override
  public SearchLocation lookupSearchLocation(StructureElement structureElement, boolean isRelative) {
    if (this.isRelative == isRelative && this.searchElement.equals(structureElement.getElementName())) {
      return searchLocation;
    }
    return null;
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences(boolean isRelative) {
    if (isRelative == this.isRelative) {
      return Collections.singletonList(new SearchLocationReference(searchLocation, searchElement, false));
    }
    return Collections.emptyList();
  }

  @Override
  public void mergeElementFinder(ElementFinder elementFinder) {
    // TODO: remember case: empty:one and empty:multiple
    List<SearchLocationReference> searchLocationReferences = elementFinder.getSeachLocationReferences(isRelative);
    if (searchElement == null) {
      if (searchLocationReferences.size() == 1) {
        SearchLocationReference searchLocationReference = searchLocationReferences.get(0);
        if (searchLocationReference.getPredicate() != null) {
          throw new RuntimeException("Cannot merge element finder with a predicate finder");
        }
        searchElement = searchLocationReference.getSearchElement();
        searchLocation = searchLocationReference.getSearchLocation();
        isRelative = searchLocationReference.isRelative();
      } else {
        MultipleElementFinder multipleArrayElementFinder = new MultipleElementFinder(getReference(), isRelative, searchElement, searchLocation);
        multipleArrayElementFinder.mergeElementFinder(elementFinder);
      }
    } else {
      MultipleElementFinder multipleArrayElementFinder = new MultipleElementFinder(getReference(), isRelative, searchElement, searchLocation);
      multipleArrayElementFinder.mergeElementFinder(elementFinder);
      // TODO: Maybe do some more checking here
    }
  }

  @Override
  public boolean isPredicate() {
    return false;
  }
}