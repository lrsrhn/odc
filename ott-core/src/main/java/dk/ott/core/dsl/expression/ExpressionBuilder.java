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
package dk.ott.core.dsl.expression;

import dk.ott.core.dsl.ObservableTreeFragment;
import dk.ott.core.dsl.TreeEdgeReference;
import dk.ott.core.dsl.adders.*;
import dk.ott.core.event.EventHandler;
import dk.ott.core.event.OnEndHandler;
import dk.ott.core.event.OnStartHandler;
import dk.ott.core.event.OnTextHandler;
import dk.ott.core.predicate.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dk.ott.core.dsl.expression.XpathParser.parseXpath;

public class ExpressionBuilder {
  private final boolean hasRoot;
  private List<TreePathAdder> treePathAdders;
  private TreeEdgeReference rootReference;
  private Predicate filter;

  public ExpressionBuilder(TreeEdgeReference rootReference, boolean hasRoot) {
    this.rootReference = rootReference;
    this.filter = null;
    this.treePathAdders = new ArrayList<TreePathAdder>();
    this.hasRoot = hasRoot;
  }

  public ExpressionBuilder elementsAbsolute(String... elements) {
    if (treePathAdders.isEmpty() && hasRoot) {
      treePathAdders.add(new TreeRootElementAdder(elements[0], false));
      elements = Arrays.copyOfRange(elements, 1, elements.length);
    }
    treePathAdders.add(new TreeElementAdder(elements, false));
    return this;
  }

  public ExpressionBuilder predicate(Predicate predicate) {
    if (treePathAdders.isEmpty()) {
      if (rootReference.getLastPredicate() != null) {
        throw new IllegalArgumentException("Two predicate finder may not be adjacent");
      }
    } else if (treePathAdders.get(treePathAdders.size() - 1) instanceof TreePredicateAdder) {
      throw new IllegalArgumentException("Two predicate finder may not be adjacent");
    }
    treePathAdders.add(new TreePredicateAdder(predicate));
    return this;
  }

  public ExpressionBuilder path(String path) {
    if (treePathAdders.isEmpty() && hasRoot) {
      treePathAdders.addAll(parseXpath(path, true));
    } else {
      treePathAdders.addAll(parseXpath(path, false));
    }
    return this;
  }

  public ExpressionBuilder filter(Predicate filter) {
    this.filter = filter;
    return this;
  }

  public ObservableTreeFragment recursion(ObservableTreeFragment observableTreeFragment) {
    this.treePathAdders.add(new TreeRecursionAdder(observableTreeFragment.getTreeEdgeReference()));
    return toFragment();
  }

  public ObservableTreeFragment handle(EventHandler eventHandler) {
    if (filter != null) {
      treePathAdders.add(new TreeElementFilterAdder(filter));
    }
    treePathAdders.add(new TreeOnStartHandlerAdder(eventHandler));
    treePathAdders.add(new TreeOnEndHandlerAdder(eventHandler));
    treePathAdders.add(new TreeOnTextHandlerAdder(eventHandler));
    return toFragment();
  }

  public ObservableTreeFragment onStart(OnStartHandler onStartHandler) {
    if (filter != null) {
      treePathAdders.add(new TreeElementFilterAdder(filter));
    }
    treePathAdders.add(new TreeOnStartHandlerAdder(onStartHandler));
    return toFragment();
  }

  public ObservableTreeFragment onText(OnTextHandler onTextHandler) {
    if (filter != null) {
      treePathAdders.add(new TreeElementTextFilterAdder(filter));
    }
    treePathAdders.add(new TreeOnTextHandlerAdder(onTextHandler));
    return toFragment();
  }

  public ObservableTreeFragment onEnd(OnEndHandler onEndHandler) {
    if (filter != null) {
      treePathAdders.add(new TreeElementFilterAdder(filter));
    }
    treePathAdders.add(new TreeOnEndHandlerAdder(onEndHandler));
    return toFragment();
  }

  public ObservableTreeFragment toFragment() {
    if (treePathAdders.isEmpty()) {
      throw new IllegalArgumentException("Missing path and predicates for expression");
    }
    TreeEdgeReference reference = rootReference;
    for (int i = 0; i < treePathAdders.size(); i++) {
      reference = treePathAdders.get(i).addTreePath(reference, i == 0 && hasRoot);
    }
    return new ObservableTreeFragment(reference);
  }
}
