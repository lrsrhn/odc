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
package dk.ott.core.dsl;

import dk.ott.core.finder.ElementFinder;
import dk.ott.core.finder.SearchLocation;
import dk.ott.core.finder.SearchLocationBuilder;

public class TreeEdgeReference {
//  private String lastSearchElement;
//  private Predicate lastPredicate;
  private ElementFinder elementFinderReference;
  private SearchLocation searchLocation;
//  private boolean isRelative;

//  public TreeEdgeReference(ElementFinder elementFinder, String lastSearchElement, boolean isRelative) {
//    this.elementFinderReference = elementFinder.getReference();
//    this.lastSearchElement = lastSearchElement;
//    this.isRelative = isRelative;
//  }
//
//  public TreeEdgeReference(ElementFinder elementFinder, Predicate lastPredicate, boolean isRelative) {
//    this.elementFinderReference = elementFinder.getReference();
//    this.lastPredicate = lastPredicate;
//    this.isRelative = isRelative;
//  }

  public TreeEdgeReference(ElementFinder elementFinder) {
    this.elementFinderReference = elementFinder.getReference();
  }

  public TreeEdgeReference(ElementFinder elementFinder, SearchLocation searchLocation) {
    this.elementFinderReference = elementFinder;
    this.searchLocation = searchLocation;
  }

  public ElementFinder getElementFinder() {
    return elementFinderReference;
  }

  public void setReference(ElementFinder reference) {
    this.elementFinderReference = reference.getReference();
  }

  public ElementFinder getElementFinderReference() {
    return elementFinderReference;
  }

  public void setElementFinderReference(ElementFinder elementFinderReference) {
    this.elementFinderReference = elementFinderReference;
  }

  public SearchLocation getSearchLocation() {
    return searchLocation;
  }

  public SearchLocationBuilder getSearchLocationBuilder() {
    return new SearchLocationBuilder(searchLocation);
  }

  public void setSearchLocation(SearchLocation searchLocation) {
    this.searchLocation = searchLocation;
  }

  //  public String getLastSearchElement() {
//    return lastSearchElement;
//  }
//
//  public void setLastSearchElement(String lastSearchElement) {
//    this.lastSearchElement = lastSearchElement;
//  }
//
//  public Predicate getLastPredicate() {
//    return lastPredicate;
//  }
//
//  public void setLastPredicate(Predicate lastPredicate) {
//    this.lastPredicate = lastPredicate;
//  }
//
//  public boolean isRelative() {
//    return isRelative;
//  }
//
//  public void setRelative(boolean relative) {
//    isRelative = relative;
//  }

  public void dereferenceElementFinder() {
    elementFinderReference = elementFinderReference.getDereference();
  }
}