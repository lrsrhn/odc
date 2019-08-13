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

public final class NodeMultipleElementEdgesArray implements Node {
  private SearcLocationList nextXmlElementFinders;
  private NodeReference thisReference;
  private boolean hasRelatives;

  NodeMultipleElementEdgesArray(NodeReference thisReference, String searchElement, Edge edge) {
    this.thisReference = thisReference;
    thisReference.setNode(this);
    this.nextXmlElementFinders = new SearcLocationList();
    if (searchElement != null) {
      hasRelatives = edge.isRelative();
      this.nextXmlElementFinders.addSearchLocation(searchElement, edge);
    }
  }

  @Override
  public EdgeBuilder buildEdge(String searchElement, boolean isRelative) {
    Edge edge = nextXmlElementFinders.lookupSearchLocation(searchElement, !isRelative);
    if (edge == null) {
      edge = new Edge(null, null, null, isRelative);
      nextXmlElementFinders.addSearchLocation(searchElement, edge);
      hasRelatives |= isRelative;
    }
    return new EdgeBuilder(this, edge);
  }

  @Override
  public EdgeBuilder buildEdge(Predicate predicate) {
    throw new UnsupportedOperationException("This operation is not supported");
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
    if (nextXmlElementFinders.isEmpty()) {
      toStringBuilder.append(previousNodesStringBuilder).append("/null\n");
      return;
    }
    int previousElementBuilderLength = previousNodesStringBuilder.length();
    for (int i = 0; i < nextXmlElementFinders.getSize(); i++) {
      previousNodesStringBuilder.setLength(previousElementBuilderLength);
      previousNodesStringBuilder
              .append(nextXmlElementFinders.edges[i].isRelative() ? "//" : "/")
              .append(nextXmlElementFinders.elementNames[i]);
      TreePrettyPrintHelper.printSearchLocation(nextXmlElementFinders.edges[i], previousNodesStringBuilder, visited, toStringBuilder);
    }
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
    return this.nextXmlElementFinders.lookupSearchLocation(elementCursor.getElementName(), includeAbsolutes);
  }

  @Override
  public Edge lookupEdge(ElementCursor elementCursor, ObjectStore objectStore) {
    return this.nextXmlElementFinders.lookupSearchLocation(elementCursor.getElementName(), true);
  }

  @Override
  public List<SearchLocationReference> getSeachLocationReferences() {
    List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(nextXmlElementFinders.getSize());
    for (int i = 0; i < nextXmlElementFinders.getSize(); i++) {
      references.add(new SearchLocationReference(nextXmlElementFinders.edges[i], nextXmlElementFinders.elementNames[i]));
    }
    return references;
  }

  @Override
  public void mergeNode(Node nodeToMerge) {
    List<SearchLocationReference> searchLocationReferences = nodeToMerge.getSeachLocationReferences();
    for (SearchLocationReference searchLocationReference : searchLocationReferences) {
      if (searchLocationReference.getPredicate() != null) {
        throw new RuntimeException("Unable to add reference using a predicate!");
      }
      Edge edge = nextXmlElementFinders.lookupSearchLocation(searchLocationReference.getSearchElement(), !searchLocationReference.isRelative());
      if (edge != null && edge.isRelative() == searchLocationReference.isRelative()) {
        edge.merge(searchLocationReference.getEdge());
      } else {
        nextXmlElementFinders.addSearchLocation(searchLocationReference.getSearchElement(), searchLocationReference.getEdge());
        hasRelatives |= searchLocationReference.isRelative();
      }
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
    for (int i = 0; i < nextXmlElementFinders.size; i++) {
      Edge edge = this.nextXmlElementFinders.edges[i];
      if (edge.getChildNode() != null) {
        Node node = edge.getChildNode().getDereference();
        edge.setChildNode(node);
        node.unreferenceTree();
      }
    }
  }

  private final class SearcLocationList {
    Edge[] edges;
    String[] elementNames;
    int[] elementNameLengths;
    private int firstRelativeIndex; // Equal to count of absolutes
    private int size;

    SearcLocationList() {
      this.edges = new Edge[4];
      this.elementNames = new String[4];
      this.elementNameLengths = new int[4];
      this.size = 0;
      this.firstRelativeIndex = size;
    }

    final void addSearchLocation(String elementName, Edge edge) {
      this.size++;
      boolean isRelative = edge.isRelative();
      edges = addItemToList(edge, edges, isRelative);
      elementNames = addItemToList(elementName.intern(), elementNames, isRelative);
      elementNameLengths = addItemToList(elementName.length(), elementNameLengths, isRelative);
      if (!isRelative) {
        firstRelativeIndex++;
      }
    }

    final Edge lookupSearchLocation(String targetElementName, boolean includeAbsolutes) {
      int targetElementNameLength = targetElementName.length();
      int firstIndex = includeAbsolutes ? 0 : firstRelativeIndex;
      for (int i = firstIndex; i < size; i++) {
        if (targetElementNameLength == elementNameLengths[i] && targetElementName.equals(elementNames[i])) {
          return nextXmlElementFinders.edges[i];
        }
      }
      return null;
    }

    private <E> E[] addItemToList(E item, E[] items, boolean isRelative) {
      if (size > items.length) {
        items = Arrays.copyOf(items, items.length + 1);
      }
      if (isRelative) {
        items[size - 1] = item;
      } else {
        for (int i = size - 1; i > firstRelativeIndex; i--) {
          items[i] = items[i - 1];
        }
        items[firstRelativeIndex] = item;
      }
      return items;
    }

    private int[] addItemToList(int item, int[] items, boolean isRelative) {
      if (size > items.length) {
        items = Arrays.copyOf(items, items.length + 1);
      }
      if (isRelative) {
        items[size - 1] = item;
      } else {
        for (int i = size - 1; i > firstRelativeIndex; i--) {
          items[i] = items[i - 1];
        }
        items[firstRelativeIndex] = item;
      }
      return items;
    }

    final boolean isEmpty() {
      return size == 0;
    }

    public final int getSize() {
      return size;
    }
  }
}
