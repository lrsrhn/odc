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

import dk.ott.core.Edge;
import dk.ott.core.Node;
import dk.ott.core.EdgeBuilder;

public class EdgeReference {
  private Edge edge;
  private Node parentNodeReference;

  public EdgeReference(Node parentNode, Edge edge) {
    this.parentNodeReference = parentNode.getReference();
    this.edge = edge;
  }

  public EdgeReference(Node parentNode) {
    this(parentNode, null);
  }

  public Node getNode() {
    return parentNodeReference;
  }

  public void setReference(Node reference) {
    this.parentNodeReference = reference.getReference();
  }

  public boolean isPredicate() {
    return parentNodeReference.isPredicate();
  }

  public Edge getEdge() {
    return edge;
  }

  public EdgeBuilder getEdgeBuilder() {
    return new EdgeBuilder(parentNodeReference, edge);
  }

  public void setEdge(Edge edge) {
    this.edge = edge;
  }

  public void dereferenceElementFinder() {
    parentNodeReference = parentNodeReference.getDereference();
  }
}