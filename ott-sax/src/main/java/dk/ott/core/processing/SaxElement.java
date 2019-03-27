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

import dk.ott.xml.TextExtractor;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

public class SaxElement implements InternalStructureElement {
  private String elementTextCache;
  private String elementNameCache;
  private String elementNamespaceCache;
  private Attributes attributes;
  private TextExtractor textExtractor;
  private XMLReader xmlReader;
  private SaxElementSkippingHandler saxElementSkippingHandler;

  public SaxElement(XMLReader xmlReader, TextExtractor textExtractor, SaxElementSkippingHandler saxElementSkippingHandler) {
    this.xmlReader = xmlReader;
    this.textExtractor = textExtractor;
    this.saxElementSkippingHandler = saxElementSkippingHandler;
  }

  public String getElementName() {
    return elementNameCache;
  }

  public String getAttributeValue(String attributeName) {
    if (attributes == null) {
      return null;
    }
    return attributes.getValue("", attributeName);
  }

  public boolean hasAttribute(String attributeName) {
    if (attributes == null) {
      return false;
    }
    return attributes.getIndex("", attributeName) > -1;
  }

  public String getText() {
    if (elementTextCache != null) {
      return elementTextCache;
    }
    elementTextCache = textExtractor.extract();
    return elementTextCache;
  }

  @Override
  public String getRawElementValue() {
    return "";
  }

  @Override
  public void skipElement() {
    saxElementSkippingHandler.resetDepth();
    xmlReader.setContentHandler(saxElementSkippingHandler);
  }

  public void clearCache() {
    this.elementTextCache = null;
  }

  public String getElementNS() {
    return elementNamespaceCache;
  }

  void setData(String namespace, String elementName, Attributes attributes) {
    this.elementNamespaceCache = namespace;
    this.elementNameCache = elementName;
    this.attributes = attributes;
  }
}
