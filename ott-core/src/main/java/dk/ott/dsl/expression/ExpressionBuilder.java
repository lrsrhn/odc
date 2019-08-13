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

import dk.ott.dsl.ObservableTreeFragment;
import dk.ott.dsl.EdgeReference;
import dk.ott.dsl.searchtree.ExpressionHelper;
import dk.ott.event.EventHandler;
import dk.ott.event.OnEndHandler;
import dk.ott.event.OnStartHandler;
import dk.ott.event.OnTextHandler;
import dk.ott.core.EdgeBuilder;
import dk.ott.predicate.Predicate;
import dk.ott.predicate.Predicates;

public class ExpressionBuilder {
  private EdgeReference edgeReference;
  private Predicate filter;
  private Predicate textFilter;

  public ExpressionBuilder(EdgeReference edgeReference) {
    this.edgeReference = edgeReference;
    this.filter = null;
  }

  public ExpressionBuilder predicate(Predicate predicate) {
    edgeReference = edgeReference.getSearchLocationBuilder()
            .addPredicateElementFinder(predicate)
            .toTreeEdgeReference();
    return this;
  }

  public ExpressionBuilder namespace(String namespace) {
    edgeReference = edgeReference.getSearchLocationBuilder()
            .addPredicateElementFinder(Predicates.namespace(namespace))
            .toTreeEdgeReference();
    return this;
  }

  public ExpressionBuilder noNamespace() {
    edgeReference = edgeReference.getSearchLocationBuilder()
            .addPredicateElementFinder(Predicates.noNamespace())
            .toTreeEdgeReference();
    return this;
  }

  public ExpressionBuilder elementPath(String elementPath) {
    edgeReference = ExpressionHelper.parseElementPath(elementPath, edgeReference, false);
    return this;
  }

  public void all(OnStartHandler onStartHandler, OnTextHandler onTextHandler, OnEndHandler onEndHandler) {
    edgeReference = edgeReference.getSearchLocationBuilder()
        .addAllElementFinder()
        .onStartHandler(onStartHandler)
        .onTextHandler(onTextHandler)
        .onEndHandler(onEndHandler)
        .toTreeEdgeReference();
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
    EdgeReference referenceToAdd = observableTreeFragmentToAdd.getEdgeReference();
    ExpressionHelper.addElementFinderSameAsReference(edgeReference, referenceToAdd)
            .mergeNode(referenceToAdd.getElementFinder());
    return toFragment();
  }

  public ObservableTreeFragment handle(EventHandler eventHandler) {
    EdgeBuilder edgeBuilder = edgeReference.getSearchLocationBuilder();
    if (filter != null) {
      edgeBuilder.filter(filter);
    }
    edgeBuilder
            .onStartHandler(eventHandler)
            .onTextHandler(eventHandler)
            .onEndHandler(eventHandler);
    return toFragment();
  }

  public ObservableTreeFragment on(OnStartHandler onStartHandler, OnTextHandler onTextHandler, OnEndHandler onEndHandler) {
    EdgeBuilder edgeBuilder = edgeReference.getSearchLocationBuilder();
    if (filter != null) {
      edgeBuilder.filter(filter);
    }
    if (onStartHandler != null) {
        edgeBuilder.onStartHandler(onStartHandler);
    }
    if (onTextHandler != null) {
      if (textFilter != null) {
        edgeBuilder.textFilter(textFilter);
      }
      edgeBuilder.onTextHandler(onTextHandler);
    }
    if (onEndHandler != null) {
      edgeBuilder.onEndHandler(onEndHandler);
    }
    return toFragment();
  }

  public ObservableTreeFragment onStart(OnStartHandler onStartHandler) {
    EdgeBuilder edgeBuilder = edgeReference.getSearchLocationBuilder();
    if (filter != null) {
      edgeBuilder.filter(filter);
    }
    edgeBuilder.onStartHandler(onStartHandler);
    return toFragment();
  }

  public ObservableTreeFragment onText(OnTextHandler onTextHandler) {
    EdgeBuilder edgeBuilder = edgeReference.getSearchLocationBuilder();
    if (filter != null) {
      edgeBuilder.filter(filter);
    }
    if (textFilter != null) {
      edgeBuilder.textFilter(textFilter);
    }
    edgeBuilder.onTextHandler(onTextHandler);
    return toFragment();
  }

  public ObservableTreeFragment onEnd(OnEndHandler onEndHandler) {
    EdgeBuilder edgeBuilder = edgeReference.getSearchLocationBuilder();
    if (filter != null) {
      edgeBuilder.filter(filter);
    }
    edgeBuilder.onEndHandler(onEndHandler);
    return toFragment();
  }

  public ObservableTreeFragment onRawText(OnTextHandler onTextHandler) {
    EdgeBuilder edgeBuilder = edgeReference.getSearchLocationBuilder();
    if (textFilter != null) {
      edgeBuilder.textFilter(textFilter);
    }
    edgeBuilder.textAsRaw().onTextHandler(onTextHandler);
    return toFragment();
  }

  public ObservableTreeFragment toFragment() {
    return new ObservableTreeFragment(edgeReference);
  }
}
