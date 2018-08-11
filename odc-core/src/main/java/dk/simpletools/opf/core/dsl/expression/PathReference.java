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
package dk.simpletools.opf.core.dsl.expression;

import dk.simpletools.opf.core.finder.ElementFinder;
import dk.simpletools.opf.core.predicate.Predicate;
import dk.simpletools.opf.core.processing.ElementFinderReference;

public class PathReference {
  private String lastSearchElement;
  private Predicate lastPredicate;
  private ElementFinderReference elementFinderReference;
  private boolean isRelative;

  public PathReference(ElementFinder elementFinder, String lastSearchElement, boolean isRelative) {
    this.elementFinderReference = elementFinder.getReference();
    this.lastSearchElement = lastSearchElement;
    this.isRelative = isRelative;
  }

  public PathReference(ElementFinder elementFinder, Predicate lastPredicate, boolean isRelative) {
    this.elementFinderReference = elementFinder.getReference();
    this.lastPredicate = lastPredicate;
    this.isRelative = isRelative;
  }

  public PathReference(ElementFinder elementFinder, boolean isRelative) {
    this.elementFinderReference = elementFinder.getReference();
    this.isRelative = isRelative;
  }

  public ElementFinder getElementFinder() {
    return elementFinderReference;
  }

  public void setReference(ElementFinder reference) {
    this.elementFinderReference = reference.getReference();
  }

  public String getLastSearchElement() {
    return lastSearchElement;
  }

  public void setLastSearchElement(String lastSearchElement) {
    this.lastSearchElement = lastSearchElement;
  }

  public Predicate getLastPredicate() {
    return lastPredicate;
  }

  public void setLastPredicate(Predicate lastPredicate) {
    this.lastPredicate = lastPredicate;
  }

  public boolean isRelative() {
    return isRelative;
  }

  public void setRelative(boolean relative) {
    isRelative = relative;
  }
}
