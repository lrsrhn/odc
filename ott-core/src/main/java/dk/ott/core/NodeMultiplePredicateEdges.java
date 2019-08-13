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
package dk.ott.core;

import dk.ott.dsl.expression.SearchLocationReference;
import dk.ott.predicate.Predicate;
import dk.ott.processing.ObjectStore;
import dk.ott.processing.ElementCursor;
import dk.ott.util.TreePrettyPrintHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class NodeMultiplePredicateEdges implements Node {
  private SearchLocationList searchLocationList;
  private NodeReference thisReference;

  public NodeMultiplePredicateEdges(NodeReference nodeReference, Predicate predicate, Edge edge) {
    this.searchLocationList = new SearchLocationList();
    this.thisReference = nodeReference;
    this.thisReference.setNode(this);
    if (predicate != null) {
      searchLocationList.addSearchLocation(edge, predicate);
    }
  }

  @Override
  public EdgeBuilder buildEdge(String searchElement, boolean isRelative) {
    throw new UnsupportedOperationException("This operation is not supported");
  }

  @Override
  public EdgeBuilder buildEdge(Predicate predicate){
    int index = searchLocationList.findIndexByPredicate(predicate);
    if (index != -1) {
      return new EdgeBuilder(this, searchLocationList.edges[index]);
    } else {
      Edge edge = new Edge(false);
      searchLocationList.addSearchLocation(edge, predicate);
      return new EdgeBuilder(this, edge);
    }
  }

  @Override
  public NodeReference getReference() {
    return thisReference;
  }

  @Override
  public Node getDereference() {
    return this;
  }

  @Override
  public void buildToString(StringBuilder previousNodesStringBuilder, Set<Node> visited, StringBuilder toStringBuilder) {
    if (this.searchLocationList.getSize() == 0) {
      toStringBuilder.append(previousNodesStringBuilder).append("/null\n");
      return;
    }
    int previousElementBuilderLength = previousNodesStringBuilder.length();
    for (int i = 0; i < this.searchLocationList.getSize(); i++) {
      previousNodesStringBuilder.setLength(previousElementBuilderLength);
      previousNodesStringBuilder
              .append('[')
              .append(searchLocationList.predicates[i].toString())
              .append(']');
      TreePrettyPrintHelper.printSearchLocation(searchLocationList.edges[i], previousNodesStringBuilder, visited, toStringBuilder);
    }
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
    for (int i = 0; i < searchLocationList.getSize(); i++) {
      if (searchLocationList.predicates[i].evaluate(elementCursor, objectStore)) {
        return searchLocationList.edges[i];
      }
    }
    return null;
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore) {
    return lookupEdge(elementCursor, objectStore, false);
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences() {
    List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(searchLocationList.getSize());
    for (int i = 0; i < searchLocationList.getSize(); i++) {
      references.add(new SearchLocationReference(searchLocationList.edges[i], searchLocationList.predicates[i]));
    }
    return references;
  }

  @Override
  public void mergeNode(Node nodeToMerge) {
    List<SearchLocationReference> searchLocationReferences = nodeToMerge.getSeachLocationReferences();
    for (SearchLocationReference searchLocationReference : searchLocationReferences) {
      if (searchLocationReference.getSearchElement() != null) {
        throw new RuntimeException("Unable to add reference using a searchElement!");
      }
      int predicateIndex = searchLocationList.findIndexByPredicate(searchLocationReference.getPredicate());
      if (predicateIndex != -1) {
        Edge edge = searchLocationList.edges[predicateIndex];
        if (edge.isRelative() == searchLocationReference.isRelative()) {
          edge.merge(searchLocationReference.getEdge());
        } else {
          searchLocationList.addSearchLocation(searchLocationReference.getEdge(), searchLocationReference.getPredicate());
        }
      } else {
        searchLocationList.addSearchLocation(searchLocationReference.getEdge(), searchLocationReference.getPredicate());
      }
    }
  }

  @Override
  public boolean isPredicate() {
    return true;
  }

  @Override
  public boolean hasRelative() {
    return false;
  }

  @Override
  public void unreferenceTree() {
    for (int i = 0; i < this.searchLocationList.size; i++) {
      Edge edge = this.searchLocationList.edges[i];
      if (edge.getChildNode() != null) {
        Node node = edge.getChildNode().getDereference();
        edge.setChildNode(node);
        node.unreferenceTree();
      }
    }
  }

  private final class SearchLocationList {
    Edge[] edges;
    Predicate[] predicates;
    private int size;

    public SearchLocationList() {
      this.edges = new Edge[4];
      this.predicates = new Predicate[4];
      this.size = 0;
    }

    public void addSearchLocation(Edge edge, Predicate predicate) {
      this.size++;
      edges = addItemToList(edge, edges);
      predicates = addItemToList(predicate, predicates);
    }

    public int findIndexByPredicate(Predicate targetPredicate) {
      for (int i = 0; i < size; i++) {
        if (predicates[i].equals(targetPredicate)) {
          return i;
        }
      }
      return -1;
    }

    private <E> E[] addItemToList(E item, E[] items) {
      if (size > items.length) {
        items = Arrays.copyOf(items, items.length + 1);
      }
      items[size - 1] = item;
      return items;
    }

    public int getSize() {
      return size;
    }
  }
}