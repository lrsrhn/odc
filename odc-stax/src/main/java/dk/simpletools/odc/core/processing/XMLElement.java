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
package dk.simpletools.odc.core.processing;

import dk.simpletools.odc.xml.XmlRawTextReader2;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLElement implements InternalStructureElement {
  private XMLStreamReader2 xmlStreamReader;
  private String elementTextCache;
  private ValueStore valueStore;
  private ObjectStore objectStore;
  private XmlRawTextReader2 xmlRawTextReader;

  public XMLElement(XMLStreamReader2 xmlStreamReader, XmlRawTextReader2 xmlRawTextReader, ObjectStore objectStore) {
    this.objectStore = objectStore == null ? new ObjectStore() : objectStore;
    this.xmlStreamReader = xmlStreamReader;
    this.valueStore = new ValueStore();
    this.xmlRawTextReader = xmlRawTextReader;
  }

  public String getElementName() {
    return xmlStreamReader.getLocalName();
  }

  public String getAttributeValue(String attributeName) {
    if (xmlStreamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
      return xmlStreamReader.getAttributeValue(null, attributeName);
    }
    return null;
  }

  public boolean hasAttribute(String attributeName) {
    if (attributeName != null) {
      for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
        if (attributeName.equals(xmlStreamReader.getAttributeLocalName(i))) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public ValueStore getValueStore() {
    return valueStore;
  }

  @Override
  public ObjectStore getObjectStore() {
    return objectStore;
  }

  @Override
  public String[] getValueArray() {
    throw new UnsupportedOperationException();
  }

  /**
   * NB: Only use this after all attributes have been read - Calling this will
   * move the XML parser forward
   * 
   */
  public String getElementValue() {
    if (elementTextCache == null) {
      try {
        elementTextCache = xmlStreamReader.getElementText();
      } catch (XMLStreamException e) {
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
        if (xmlStreamReader.getEventType() != XMLStreamConstants.START_ELEMENT) {
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

  @Override
  public void skipElement() {
    try {
      xmlStreamReader.skipElement();
    } catch (XMLStreamException xse) {
      throw new RuntimeException(xse);
    }
  }

  public void clearCache() {
    elementTextCache = null;
  }

  public String getElementNS() {
        return xmlStreamReader.getNamespaceURI();
    }
}