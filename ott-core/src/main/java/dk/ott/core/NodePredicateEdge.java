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

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NodePredicateEdge extends NodeBase {
  private Predicate predicate;
  private Edge edge;

  public NodePredicateEdge() {
    super();
    this.edge = new Edge(false);
  }

  @Override
  public EdgeBuilder buildEdge(Predicate predicate) {
    if (this.predicate == null) {
      this.predicate = predicate;
      this.edge.setRelative(false);
      return new EdgeBuilder(thisReference, edge);
    } else if (this.predicate.equals(predicate)) {
      return new EdgeBuilder(thisReference, edge);
    }
    NodeMultiplePredicateEdges nodeMultiplePredicateEdges = new NodeMultiplePredicateEdges(thisReference, this.predicate, edge, otherwise);
    return nodeMultiplePredicateEdges.buildEdge(predicate);
  }

  @Override
  public void buildToString(StringBuilder previousNodesStringBuilder, Set<Node> visited, StringBuilder toStringBuilder) {
    if (this.edge == null || this.predicate == null) {
      toStringBuilder.append(previousNodesStringBuilder).append("/null\n");
      return;
    }
    previousNodesStringBuilder.append('[').append(predicate.toString()).append(']');
    TreePrettyPrintHelper.printSearchLocation(edge, previousNodesStringBuilder, visited, toStringBuilder);
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
    return lookupEdge(elementCursor, objectStore);
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore) {
    if (predicate.evaluate(elementCursor, objectStore)) {
      return edge;
    }
    return null;
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences() {
    if (predicate != null) {
      return Collections.singletonList(new SearchLocationReference(edge, predicate));
    }
    return Collections.emptyList();
  }

  @Override
  public void mergeNode(Node nodeToMerge) {
    List<SearchLocationReference> searchLocationReferences = nodeToMerge.getSeachLocationReferences();
    if (searchLocationReferences.size() > 1) {
      NodeMultiplePredicateEdges nodeMultiplePredicateEdges = new NodeMultiplePredicateEdges(thisReference, predicate, edge, otherwise);
      nodeMultiplePredicateEdges.mergeNode(nodeToMerge);
    } else if (searchLocationReferences.size() ==  1) {
      SearchLocationReference searchLocationReference = searchLocationReferences.get(0);
      if (searchLocationReference.getSearchElement() != null) {
        throw new IllegalStateException("Cannot do search elements");
      }
      if (edge != null) {
        if (predicate == null || new SearchLocationReference(edge, predicate).same(searchLocationReference)) {
          edge.merge(searchLocationReference.getEdge());
        } else {
          NodeMultiplePredicateEdges nodeMultiplePredicateEdges = new NodeMultiplePredicateEdges(thisReference, predicate, edge, otherwise);
          nodeMultiplePredicateEdges.mergeNode(nodeToMerge);
        }
      } else {
       edge = searchLocationReference.getEdge();
       predicate = searchLocationReference.getPredicate();
      }
    }

    NodeMultiplePredicateEdges nodeMultiplePredicateEdges = new NodeMultiplePredicateEdges(getReference(), predicate, edge, otherwise);
    nodeMultiplePredicateEdges.mergeNode(nodeToMerge);
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
    if (edge.getChildNode() != null) {
      Node node = edge.getChildNode().getDereference();
      edge.setChildNode(node);
      node.unreferenceTree();
    }
  }
}
