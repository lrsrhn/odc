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
import dk.ott.core.dsl.searchtree.ExpressionHelper;
import dk.ott.core.event.EventHandler;
import dk.ott.core.event.OnEndHandler;
import dk.ott.core.event.OnStartHandler;
import dk.ott.core.event.OnTextHandler;
import dk.ott.core.finder.ElementFinder;
import dk.ott.core.finder.SearchLocationBuilder;
import dk.ott.core.predicate.Predicate;

public class ExpressionBuilder {
  private boolean hasRoot;
  private TreeEdgeReference treeEdgeReference;
  private Predicate filter;
  private Predicate textFilter;

  public ExpressionBuilder(TreeEdgeReference treeEdgeReference, boolean hasRoot) {
    this.treeEdgeReference = treeEdgeReference;
    this.filter = null;
    this.hasRoot = hasRoot;
  }

  public ExpressionBuilder predicate(Predicate predicate) {
    ElementFinder elementFinder = ExpressionHelper.addNextPredicate(treeEdgeReference).setPredicate(predicate);
    this.treeEdgeReference = new TreeEdgeReference(elementFinder, predicate, false);
    return this;
  }

  public ExpressionBuilder elementPath(String elementPath) {
    treeEdgeReference = ExpressionHelper.parseElementPath(elementPath, treeEdgeReference, hasRoot);
    hasRoot = false;
    return this;
  }

  public ExpressionBuilder filter(Predicate filter) {
    this.filter = filter;
    return this;
  }

  public ExpressionBuilder textFilter(Predicate textFilter) {
    this.textFilter = textFilter;
    return this;
  }

  public ObservableTreeFragment recursion(ObservableTreeFragment observableTreeFragmentRecursive) {
    return addReference(observableTreeFragmentRecursive);
  }

    public ObservableTreeFragment addReference(ObservableTreeFragment observableTreeFragmentToAdd) {
    TreeEdgeReference referenceToAdd = observableTreeFragmentToAdd.getTreeEdgeReference();
    ExpressionHelper.addElmentFinderCopy(treeEdgeReference, referenceToAdd)
            .mergeElementFinder(referenceToAdd.getElementFinder());
    return toFragment();
  }

  public ObservableTreeFragment handle(EventHandler eventHandler) {
    SearchLocationBuilder searchLocationBuilder = ExpressionHelper.getSearchLocationBuilder(treeEdgeReference);
    if (filter != null) {
      searchLocationBuilder.filter(filter);
    }
    searchLocationBuilder
            .onStartHandler(eventHandler)
            .onTextHandler(eventHandler)
            .onEndHandler(eventHandler);
    return toFragment();
  }

  public ObservableTreeFragment on(OnStartHandler onStartHandler, OnTextHandler onTextHandler, OnEndHandler onEndHandler) {
    SearchLocationBuilder searchLocationBuilder = ExpressionHelper.getSearchLocationBuilder(treeEdgeReference);
    if (filter != null) {
      searchLocationBuilder.filter(filter);
    }
    if (onStartHandler != null) {
        searchLocationBuilder.onStartHandler(onStartHandler);
    }
    if (onTextHandler != null) {
      if (textFilter != null) {
        searchLocationBuilder.textFilter(textFilter);
      }
      searchLocationBuilder.onTextHandler(onTextHandler);
    }
    if (onEndHandler != null) {
      searchLocationBuilder.onEndHandler(onEndHandler);
    }
    return toFragment();
  }

  public ObservableTreeFragment onStart(OnStartHandler onStartHandler) {
    SearchLocationBuilder searchLocationBuilder = ExpressionHelper.getSearchLocationBuilder(treeEdgeReference);
    if (filter != null) {
      searchLocationBuilder.filter(filter);
    }
    searchLocationBuilder.onStartHandler(onStartHandler);
    return toFragment();
  }

  public ObservableTreeFragment onText(OnTextHandler onTextHandler) {
    SearchLocationBuilder searchLocationBuilder = ExpressionHelper.getSearchLocationBuilder(treeEdgeReference);
    if (filter != null) {
      searchLocationBuilder.filter(filter);
    }
    if (textFilter != null) {
      searchLocationBuilder.textFilter(textFilter);
    }
    searchLocationBuilder.onTextHandler(onTextHandler);
    return toFragment();
  }

  public ObservableTreeFragment onEnd(OnEndHandler onEndHandler) {
    SearchLocationBuilder searchLocationBuilder = ExpressionHelper.getSearchLocationBuilder(treeEdgeReference);
    if (filter != null) {
      searchLocationBuilder.filter(filter);
    }
    searchLocationBuilder.onEndHandler(onEndHandler);
    return toFragment();
  }

  public ObservableTreeFragment onRawText(OnTextHandler onTextHandler) {
    SearchLocationBuilder searchLocationBuilder = ExpressionHelper.getSearchLocationBuilder(treeEdgeReference);
    if (textFilter != null) {
      searchLocationBuilder.textFilter(textFilter);
    }
    searchLocationBuilder.textAsRaw().onTextHandler(onTextHandler);
    return toFragment();
  }

  public ObservableTreeFragment toFragment() {
    return new ObservableTreeFragment(treeEdgeReference);
  }
}
