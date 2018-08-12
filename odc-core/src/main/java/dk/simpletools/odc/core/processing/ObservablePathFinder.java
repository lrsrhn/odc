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
package dk.simpletools.odc.core.processing;

import dk.simpletools.odc.core.dsl.expression.ExpressionBuilder;
import dk.simpletools.odc.core.dsl.expression.PathReference;
import dk.simpletools.odc.core.dsl.searchtree.RootTreeBuilder;
import dk.simpletools.odc.core.finder.ElementFinder;
import dk.simpletools.odc.core.finder.RootElementFinder;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public abstract class ObservablePathFinder {
  protected PathReference rootElementFinder;

  public ObservablePathFinder() {
    this.rootElementFinder = new PathReference(new RootElementFinder().getReference(), false);
  }

  ElementFinder getRootElementFinder() {
    return rootElementFinder.getElementFinder();
  }

  public RootTreeBuilder treeBuilder() {
    return new RootTreeBuilder(rootElementFinder.getElementFinder().getReference());
  }

  public ExpressionBuilder addXpath(String xpath) {
    return new ExpressionBuilder(rootElementFinder, true).path(xpath);
  }

  public ExpressionBuilder addElements(String...elements) {
    return new ExpressionBuilder(rootElementFinder, true).elementsAbsolute(elements);
  }

  public ObjectStore find(String rawXml) {
    return find(new StringReader(rawXml));
  }

  public abstract ObjectStore find(Reader reader);

  @Override
  public String toString() {
    try {
      StringBuilder tostring = new StringBuilder();
      Set<ElementFinder> visited = new HashSet<ElementFinder>();
      rootElementFinder.getElementFinder().buildToString(new StringBuilder(), visited, tostring);
      return tostring.toString();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}