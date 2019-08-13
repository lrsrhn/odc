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
import dk.ott.dsl.expression.RootExpressionBuilder;
import dk.ott.dsl.searchtree.ElementTreeBuilder;
import dk.ott.dsl.searchtree.OnlyElementTreeBuilder;
import dk.ott.dsl.searchtree.RootSubTreeBuilder;
import dk.ott.core.Node;
import dk.ott.core.NodeElementEdge;
import dk.ott.core.NodePredicateEdge;
import dk.ott.predicate.Predicate;

public class ObservableRootTreeFragment {
  private Node node;

  private RootExpressionBuilder expression(Node newNode) {
    if (this.node == null) {
      this.node = newNode;
    }
    return new RootExpressionBuilder(this.node);
  }

  private RootSubTreeBuilder treeBuilder(Node newNode) {
    if (node == null) {
      this.node = newNode;
    }
    return new RootSubTreeBuilder(node);
  }

  public ElementTreeBuilder<RootSubTreeBuilder> element(String elementName) {
    return treeBuilder(new NodeElementEdge().getReference()).element(elementName);
  }

  public OnlyElementTreeBuilder<RootSubTreeBuilder> predicate(Predicate predicate) {
    return treeBuilder(new NodePredicateEdge().getReference()).predicate(predicate);
  }

  public ExpressionBuilder elementPath(String elementPath) {
    return expression(new NodeElementEdge().getReference()).elementPath(elementPath);
  }

  public ExpressionBuilder predicateExp(Predicate predicate) {
    return expression(new NodePredicateEdge().getReference()).predicate(predicate);
  }

  public EdgeReference getTreeEdgeReference() {
    return new EdgeReference(node);
  }
}
