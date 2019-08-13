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
package dk.ott.dsl;

import dk.ott.dsl.expression.ExpressionBuilder;
import dk.ott.dsl.searchtree.ElementTreeBuilder;
import dk.ott.dsl.searchtree.OnlyElementTreeBuilder;
import dk.ott.dsl.searchtree.SubTreeBuilder;
import dk.ott.predicate.Predicate;

import java.util.HashMap;

public class ObservableTreeFragment {
  private EdgeReference edgeReference;

  public ObservableTreeFragment(EdgeReference edgeReference) {
    this.edgeReference = edgeReference;
  }

  private ExpressionBuilder expression() {
    return new ExpressionBuilder(edgeReference);
  }

  public SubTreeBuilder treeBuilder() {
    return new SubTreeBuilder(new HashMap<String, EdgeReference>(), edgeReference);
  }

  public ElementTreeBuilder<SubTreeBuilder> element(String elementName) {
    return treeBuilder().element(elementName);
  }

  public OnlyElementTreeBuilder<SubTreeBuilder> predicate(Predicate predicate) {
    return treeBuilder().predicate(predicate);
  }

  public ExpressionBuilder elementPath(String elementPath) {
    return expression().elementPath(elementPath);
  }

  public ExpressionBuilder predicateExp(Predicate predicate) {
    return expression().predicate(predicate);
  }

  public EdgeReference getEdgeReference() {
    return edgeReference;
  }

}
