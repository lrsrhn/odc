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

import java.util.*;

public class NodeMultipleElementEdges extends NodeBase {
  private HashMap<String, Edge> nextXmlElementFinders;
  private boolean hasRelatives;

  NodeMultipleElementEdges(NodeReference thisReference, String searchElement, Edge edge, Edge otherwise) {
    super(thisReference, otherwise);
    this.nextXmlElementFinders = new HashMap<String, Edge>(4);
    hasRelatives = edge.isRelative();
    this.nextXmlElementFinders.put(searchElement.intern(), edge);
  }

  @Override
  public EdgeBuilder buildEdge(String searchElement, boolean isRelative) {
    Edge edge = nextXmlElementFinders.get(searchElement);
    if (edge == null || edge.isRelative() != isRelative) {
      edge = new Edge(isRelative);
      nextXmlElementFinders.put(searchElement.intern(), edge);
      hasRelatives |= isRelative;
    }
    return new EdgeBuilder(this, edge);
  }

  @Override
  public void buildToString(StringBuilder previousNodesStringBuilder, Set<Node> visited, StringBuilder toStringBuilder) {
    if (nextXmlElementFinders.isEmpty()) {
      toStringBuilder.append(previousNodesStringBuilder).append("/null\n");
      return;
    }
    int previousElementBuilderLength = previousNodesStringBuilder.length();
    for (Map.Entry<String, Edge> entries : nextXmlElementFinders.entrySet()) {
      previousNodesStringBuilder.setLength(previousElementBuilderLength);
      previousNodesStringBuilder
              .append(entries.getValue().isRelative() ? "//" : "/")
              .append(entries.getKey());
      TreePrettyPrintHelper.printSearchLocation(entries.getValue(), previousNodesStringBuilder, visited, toStringBuilder);
    }
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
    String targetElementName = elementCursor.getElementName();
    Edge edge = this.nextXmlElementFinders.get(targetElementName);
    if (edge != null && edge.isRelative() | includeAbsolutes) {
      return edge;
    }
    return null;
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore) {
    return lookupEdge(elementCursor, objectStore, true);
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences() {
    List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(nextXmlElementFinders.size());
    for (Map.Entry<String, Edge> entry : nextXmlElementFinders.entrySet()) {
      references.add(new SearchLocationReference(entry.getValue(), entry.getKey()));
    }
    return references;
  }

  @Override
  public void mergeNode(Node nodeToMerge) {
    mergeElementFinder(nodeToMerge, false);
    mergeElementFinder(nodeToMerge, true);
  }

  private void mergeElementFinder(Node node, boolean isRelative) {
    List<SearchLocationReference> searchLocationReferences = node.getSeachLocationReferences();
    for (SearchLocationReference searchLocationReference : searchLocationReferences) {
      if (searchLocationReference.getPredicate() != null) {
        throw new RuntimeException("Unable to add reference using a predicate!");
      }
      Edge edge = nextXmlElementFinders.get(searchLocationReference.getSearchElement());
      if (edge != null && edge.isRelative() == isRelative) {
        edge.merge(searchLocationReference.getEdge());
      } else {
        nextXmlElementFinders.put(searchLocationReference.getSearchElement(), searchLocationReference.getEdge());
      }
      hasRelatives |= isRelative;
    }
  }

  @Override
  public boolean isPredicate() {
    return false;
  }

  @Override
  public boolean hasRelative() {
    return hasRelatives;
  }

  @Override
  public void unreferenceTree() {
    for (Edge edge : nextXmlElementFinders.values()) {
      if (edge.getChildNode() != null) {
        Node node = edge.getChildNode().getDereference();
        edge.setChildNode(node);
        node.unreferenceTree();
      }
    }
    for (Edge edge : nextXmlElementFinders.values()) {
      if (edge.getChildNode() != null) {
        Node node = edge.getChildNode().getDereference();
        edge.setChildNode(node);
        node.unreferenceTree();
      }
    }
  }

  private static void buildToStringForMap(boolean isRelative, StringBuilder previousElementsBuilder, Set<Node> visited, StringBuilder toStringBuilder, Map<String, Edge> elementFinders) {
    int previousElementBuilderLength = previousElementsBuilder.length();
    for (Map.Entry<String, Edge> entries : elementFinders.entrySet()) {
      previousElementsBuilder.setLength(previousElementBuilderLength);
      previousElementsBuilder
              .append(isRelative ? "//" : "/")
              .append(entries.getKey());
      TreePrettyPrintHelper.printSearchLocation(entries.getValue(), previousElementsBuilder, visited, toStringBuilder);
    }
  }
}
