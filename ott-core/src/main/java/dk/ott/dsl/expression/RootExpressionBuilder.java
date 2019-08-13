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
package dk.ott.dsl.expression;

import dk.ott.core.Node;
import dk.ott.core.NodeReference;
import dk.ott.dsl.EdgeReference;
import dk.ott.dsl.ObservableTreeFragment;
import dk.ott.dsl.searchtree.ExpressionHelper;
import dk.ott.event.OnEndHandler;
import dk.ott.event.OnStartHandler;
import dk.ott.event.OnTextHandler;
import dk.ott.predicate.Predicate;
import dk.ott.predicate.Predicates;

public class RootExpressionBuilder {
  private NodeReference rootNodeReference;

  public RootExpressionBuilder(Node rootNode) {
    this.rootNodeReference = rootNode.getReference();
  }

  public ExpressionBuilder predicate(Predicate predicate) {
    return new ExpressionBuilder(
            rootNodeReference
            .buildEdge(predicate)
            .toTreeEdgeReference()
    );
  }

  public ExpressionBuilder namespace(String namespace) {
    return new ExpressionBuilder(
            rootNodeReference
            .buildEdge(Predicates.namespace(namespace))
            .toTreeEdgeReference()
    );
  }

  public ExpressionBuilder noNamespace() {
    return new ExpressionBuilder(
            rootNodeReference
            .buildEdge(Predicates.noNamespace())
            .toTreeEdgeReference()
    );
  }

  public ExpressionBuilder elementPath(String elementPath) {
    return new ExpressionBuilder(
            ExpressionHelper.parseElementPath(elementPath, new EdgeReference(rootNodeReference), true));
  }

  public void all(OnStartHandler onStartHandler, OnTextHandler onTextHandler, OnEndHandler onEndHandler) {
//    rootNodeReference = rootNodeReference.
//        .addAllElementFinder()
//        .onStartHandler(onStartHandler)
//        .onTextHandler(onTextHandler)
//        .onEndHandler(onEndHandler)
//      .toTreeEdgeReference();
  }

  public ObservableTreeFragment recursion(ObservableTreeFragment observableTreeFragmentRecursive) {
    return addReference(observableTreeFragmentRecursive);
  }

    public ObservableTreeFragment addReference(ObservableTreeFragment observableTreeFragmentToAdd) {
      rootNodeReference.mergeNode(observableTreeFragmentToAdd.getEdgeReference().getElementFinder());
      return toFragment();
  }

  public ObservableTreeFragment toFragment() {
    return new ObservableTreeFragment(new EdgeReference(rootNodeReference));
  }
}
