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
package dk.ott.core.processing;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static dk.ott.core.processing.TextTrimmer.trimToNull;

public class DomElementCursor implements InternalElementCursor {
  private Node node;
  private boolean stopProcessing;
  private String elementTextCache;
  protected String elementNamespaceCache;

  void setNode(Node node) {
    this.node = node;
  }

  void setNodeText(Node nodeText) {
    if (elementTextCache == null) {
      if (nodeText == null) {
        elementTextCache = null;
        return;
      }
      this.elementTextCache = trimToNull(nodeText.getTextContent());
    }
  }

  public String getElementName() {
    return node.getLocalName();
  }

  public String getAttributeValue(String attributeName) {
    NamedNodeMap attributes = node.getAttributes();
    if (attributes == null) {
      return null;
    }
    Node node = attributes.getNamedItem(attributeName);
    if (node == null) {
      return null;
    }
    return trimToNull(node.getNodeValue());
  }

  public boolean hasAttribute(String attributeName) {
    NamedNodeMap attributes = node.getAttributes();
    return attributes != null && attributes.getNamedItem(attributeName) != null;
  }

  public String getText() {
    return elementTextCache;
  }

  @Override
  public void stopProcessing() {
    this.stopProcessing = true;
  }

  @Override
  public boolean mustStopProcessing() {
    return stopProcessing;
  }

  @Override
  public void clearCache() {
    elementTextCache = null;
    elementNamespaceCache = null;
  }

  public String getElementNS() {
    if (elementNamespaceCache == null) {
      elementNamespaceCache = trimToNull(node.getNamespaceURI());
      return elementNamespaceCache;
    }
    return elementNamespaceCache;
  }
}
