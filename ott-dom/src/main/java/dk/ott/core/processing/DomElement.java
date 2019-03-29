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

public class DomElement implements InternalStructureElement {
  private Node node;

  public void setNode(Node node) {
    this.node = node;
  }

  public String getElementName() {
    return node.getLocalName();
  }

  public String getAttributeValue(String attributeName) {
    NamedNodeMap attributes = node.getAttributes();
    if (attributes == null) {
      return null;
    }
    return attributes.getNamedItem(attributeName).getNodeValue();
  }

  public boolean hasAttribute(String attributeName) {
    NamedNodeMap attributes = node.getAttributes();
    if (attributes == null) {
      return false;
    }
    return attributes.getNamedItem(attributeName) != null;
  }

  public String getText() {
    return node.getTextContent();
  }

  /**
   * NB: Only use this after all attributes have been read - Calling this will
   * move the XML parser forward
   */
  @Override
  public String getRawElementValue() {
    return "";
  }

  @Override
  public void skipElement() {
    // TODO
  }

  public void clearCache() {
    // Do nothing
  }

  public String getElementNS() {
    return node.getNamespaceURI();
  }
}
