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

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SinglePredicateMatchFinder implements ElementFinder {
  private Predicate predicate;
  private SearchLocation searchLocation;
  private ElementFinderReference thisReference;

  public SinglePredicateMatchFinder() {
    this.searchLocation = new SearchLocation();
    this.thisReference = new ElementFinderReference(this);
  }

  @Override
  public ElementFinder addNextElementFinder(String searchElement, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public ElementFinder addNextElementFinder(Predicate predicate, boolean isRelative) {
    if (this.predicate == null || (this.predicate.equals(predicate) && searchLocation.getElementFinder() == null)) {
      this.predicate = predicate;
      searchLocation.setElementFinder(new SingleElementFinder().getReference());
    } else if (!this.predicate.equals(predicate)) {
      MultiplePredicateMatchFinder multiplePredicateMatchFinder = new MultiplePredicateMatchFinder(thisReference, this.predicate, searchLocation);
      return multiplePredicateMatchFinder.addNextElementFinder(predicate, isRelative);
    }
    return searchLocation.getElementFinder();
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
      this.searchLocation = new SearchLocation();
      return new SearchLocationBuilder(this.searchLocation);
    } else if (this.predicate.equals(predicate)) {
      if (searchLocation == null) {
        this.searchLocation = new SearchLocation();
      }
      return new SearchLocationBuilder(this.searchLocation);
    } else {
      MultiplePredicateMatchFinder multiplePredicateMatchFinder = new MultiplePredicateMatchFinder(thisReference, this.predicate, searchLocation);
      return multiplePredicateMatchFinder.buildSearchLocation(predicate);
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
  public SearchLocation lookupSearchLocation(Predicate predicate) {
    if (this.predicate.equals(predicate)) {
      return searchLocation;
    }
    return null;
  }

  @Override
  public void buildToString(StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder) {
//    if (this.nextElementFinder == null && this.predicate == null && this.elementHandler == null) {
//      toStringBuilder.append(previousElementsBuilder).append("/null\n");
//      return;
//    }
//    previousElementsBuilder.append('[').append(predicate.toString()).append(']');
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
    if (!isRelative && predicate.evaluate(structureElement)) {
      return searchLocation;
    }
    return null;
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences(boolean isRelative) {
    if (predicate != null) {
      return Collections.singletonList(new SearchLocationReference(searchLocation, predicate, false));
    }
    return Collections.emptyList();
  }

  @Override
  public void mergeElementFinder(ElementFinder elementFinder) {
    MultiplePredicateMatchFinder multiplePredicateMatchFinder = new MultiplePredicateMatchFinder(getReference(), predicate, searchLocation);
    multiplePredicateMatchFinder.mergeElementFinder(elementFinder);
  }

  @Override
  public boolean isPredicate() {
    return true;
  }
}
