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

import dk.ott.xml.XmlRawTextReader2;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLElement implements InternalStructureElement {
  private XMLStreamReader2 xmlStreamReader;
  private String elementTextCache;
  private String elementNameCache;
  private String elementNamespaceCache;
  private XmlRawTextReader2 xmlRawTextReader;
  private int eventType;

  public XMLElement(XMLStreamReader2 xmlStreamReader, XmlRawTextReader2 xmlRawTextReader) {
    this.xmlStreamReader = xmlStreamReader;
    this.xmlRawTextReader = xmlRawTextReader;
  }

  public void setEventType(int eventType) {
    this.eventType = eventType;
  }

  public String getElementName() {
    if (elementNameCache == null) {
      elementNameCache = xmlStreamReader.getLocalName();
    }
    return elementNameCache;
  }

  public String getAttributeValue(String attributeName) {
    if (eventType == XMLStreamReader.START_ELEMENT) {
      return xmlStreamReader.getAttributeValue(null, attributeName);
    }
    return null;
  }

  public boolean hasAttribute(String attributeName) {
    if (attributeName != null && eventType == XMLStreamReader.START_ELEMENT) {
      for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
        if (attributeName.equals(xmlStreamReader.getAttributeLocalName(i))) {
          return true;
        }
      }
    }
    return false;
  }

  public String getText() {
    if (elementTextCache != null) {
      return elementTextCache;
    }
    if (eventType == XMLStreamConstants.CHARACTERS || eventType == XMLStreamConstants.CDATA) {
      try {
        elementTextCache = xmlStreamReader.getText();
      } catch (IllegalStateException e) {
        // Element has children and does not have a value/text
      }
    }
    return elementTextCache;
  }

  /**
   * NB: Only use this after all attributes have been read - Calling this will
   * move the XML parser forward
   *
   */
  @Override
  public String getRawElementValue() {
    try {
      if (elementTextCache == null) {
        if (eventType != XMLStreamConstants.START_ELEMENT) {
          throw new RuntimeException("Cannot retrieve raw value from other event then Start_element");
        }
        xmlRawTextReader.setStartIndex(xmlStreamReader.getLocation().getCharacterOffset());
        xmlStreamReader.skipElement();
        elementTextCache = xmlRawTextReader.readRawText(xmlStreamReader.getLocation().getCharacterOffset());
      }
      return elementTextCache;
    } catch (XMLStreamException xse) {
      throw new RuntimeException(xse);
    }
  }

  public void clearCache() {
    elementNamespaceCache = null;
    elementNameCache = null;
    elementTextCache = null;
  }

  public String getElementNS() {
    if (elementNamespaceCache == null) {
      elementNamespaceCache = xmlStreamReader.getNamespaceURI();
    }
    return elementNamespaceCache;
  }
}
