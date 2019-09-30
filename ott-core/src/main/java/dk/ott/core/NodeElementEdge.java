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
import dk.ott.processing.ElementCursor;
import dk.ott.processing.ObjectStore;
import dk.ott.util.TreePrettyPrintHelper;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class NodeElementEdge extends NodeBase {
  private String searchElement;
  private Edge edge;

  public NodeElementEdge() {
    super();
    this.edge = new Edge(false);
  }

  @Override
  public EdgeBuilder buildEdge(String searchElement, boolean isRelative) {
    if (this.searchElement == null) {
      this.searchElement = searchElement.intern();
      this.edge.setRelative(isRelative);
      return new EdgeBuilder(thisReference, edge);
    } else if (this.searchElement.equals(searchElement) && edge.isRelative() == isRelative) {
      return new EdgeBuilder(thisReference, edge);
    }
    NodeMultipleElementEdgesArray multipleXmlElementFinder = new NodeMultipleElementEdgesArray(thisReference, this.searchElement, edge, otherwise);
    return multipleXmlElementFinder.buildEdge(searchElement, isRelative);
  }

  @Override
  public void buildToString(StringBuilder previousNodesStringBuilder, Set<Node> visited, StringBuilder toStringBuilder) {
    if (searchElement == null) {
      toStringBuilder.append(previousNodesStringBuilder).append("/null\n");
      return;
    }
    previousNodesStringBuilder
            .append(edge.isRelative() ? "//" : "/")
            .append(searchElement);
    TreePrettyPrintHelper.printSearchLocation(edge, previousNodesStringBuilder, visited, toStringBuilder);
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
    return includeAbsolutes | edge.isRelative() ? lookupEdge(elementCursor, objectStore) : null;
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore) {
      if (searchElement.equals(elementCursor.getElementName())) {
      return edge;
    }
    return null;
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences() {
    if (edge != null) {
      return Collections.singletonList(new SearchLocationReference(edge, searchElement));
    }
    return Collections.emptyList();
  }

  @Override
  public void mergeNode(Node nodeToMerge) {
    List<SearchLocationReference> searchLocationReferences = nodeToMerge.getSeachLocationReferences();
    if (searchLocationReferences.size() > 1) {
      NodeMultipleElementEdgesArray nodeMultipleElementEdgesArray = new NodeMultipleElementEdgesArray(thisReference, searchElement, edge, otherwise);
      nodeMultipleElementEdgesArray.mergeNode(nodeToMerge);
    } else if (searchLocationReferences.size() ==  1) {
      SearchLocationReference searchLocationReference = searchLocationReferences.get(0);
      if (searchLocationReference.getPredicate() != null) {
        throw new IllegalStateException("Cannot do predicate");
      }
      if (edge != null) {
        if (searchElement == null || new SearchLocationReference(edge, searchElement).same(searchLocationReference)) {
          edge.merge(searchLocationReference.getEdge());
        } else {
          NodeMultipleElementEdgesArray nodeMultipleElementEdgesArray = new NodeMultipleElementEdgesArray(thisReference, searchElement, edge, otherwise);
          nodeMultipleElementEdgesArray.mergeNode(nodeToMerge);
        }
      } else {
       edge = searchLocationReference.getEdge();
       searchElement = searchLocationReference.getSearchElement();
      }
    }
  }

  @Override
  public boolean isPredicate() {
    return false;
  }

  @Override
  public boolean hasRelative() {
    return edge.isRelative();
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
