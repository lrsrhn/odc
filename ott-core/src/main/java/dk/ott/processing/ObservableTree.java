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
package dk.ott.processing;

import dk.ott.bintree.BinTree;
import dk.ott.core.Node;
import dk.ott.dsl.EdgeReference;
import dk.ott.dsl.ObservableRootTreeFragment;
import dk.ott.dsl.expression.ExpressionBuilder;
import dk.ott.dsl.searchtree.RootTreeBuilder;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public abstract class ObservableTree {
  protected BinTree binTree;

  public ObservableTree(BinTree binTree) {
    this.binTree = binTree;
  }

//  Node getRootEdgeReference() {
//    return rootEdgeReference.getNode();
//  }

  public RootTreeBuilder treeBuilder() {
//    return new RootTreeBuilder(rootEdgeReference.getNode().getReference());
    return null;
  }

  public static ObservableRootTreeFragment detachedTree() {
//    return new ObservableRootTreeFragment();
    return null;
  }

  public ExpressionBuilder elementPath(String elementPath) {
//    return new RootExpressionBuilder(rootEdgeReference.getNode()).elementPath(elementPath);
    return null;
  }

  public ObservableTree addSubTree(EdgeReference edgeReference) {
//    rootEdgeReference.getNode().mergeNode(edgeReference.getNode());
    return this;
  }

  public ObjectStore find(String rawXml) {
    return find(new StringReader(rawXml), new ObjectStore());
  }
  public ObjectStore find(String rawXml, ObjectStore objectStore) { return find(new StringReader(rawXml), objectStore); }
  public ObjectStore find(Reader reader) { return find(reader, new ObjectStore()); }

  public abstract ObjectStore find(Reader reader, ObjectStore objectStore);

  @Override
  public String toString() {
    try {
      StringBuilder tostring = new StringBuilder();
      Set<Node> visited = new HashSet<Node>();
//      rootEdgeReference.getNode().buildToString(new StringBuilder(), visited, tostring);
      return tostring.toString();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}