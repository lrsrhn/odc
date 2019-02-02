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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XMLElement implements InternalStructureElement {
  private XmlPullParser xmlPullParser;
  private String elementTextCache;
  private ValueStore valueStore;
  private ObjectStore objectStore;

  public XMLElement(XmlPullParser xmlStreamReader, ValueStore valueStore) {
    this.valueStore = valueStore == null ? new ValueStore() : valueStore;
    this.xmlPullParser = xmlStreamReader;
    this.objectStore = new ObjectStore();
  }

  public String getElementName() {
    return xmlPullParser.getName();
  }

  public String getAttributeValue(String attributeName) {
    try {
      if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
        return xmlPullParser.getAttributeValue(null, attributeName);
      }
      return null;
    } catch (XmlPullParserException xe) {
      throw new RuntimeException(xe);
    }
  }

  public boolean hasAttribute(String attributeName) {
    if (attributeName != null) {
      for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
        if (attributeName.equalsIgnoreCase(xmlPullParser.getAttributeName(i))) {
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
    if (elementTextCache != null) {
      return elementTextCache;
    }
    try {
      StringBuilder stringBuilder = new StringBuilder(128);
      if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
        while(xmlPullParser.next() == XmlPullParser.TEXT) {
          stringBuilder.append(xmlPullParser.getText());
        }
      }
      elementTextCache = stringBuilder.toString();
      return elementTextCache;
    } catch (Exception e) {
      // Element has children and does not have a value/text
      return elementTextCache;
    }
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
        if (xmlPullParser.getEventType() != XmlPullParser.START_TAG) {
          throw new RuntimeException("Cannot retrieve raw value from other event then Start_element");
        }
        elementTextCache = moveCursorToEndElement();
      }
      return elementTextCache;
    } catch (Exception xse) {
      throw new RuntimeException(xse);
    }
  }

  private String moveCursorToEndElement() {
    try {
      StringBuilder builder = new StringBuilder(128);
      int currentDepth = 0;
      xmlPullParser.next(); // Skip current element
      // Process all child elements if necessary
      while(xmlPullParser.getEventType() != XmlPullParser.END_TAG || (xmlPullParser.getEventType() == XmlPullParser.END_TAG && currentDepth > 0)) {
        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
          currentDepth++;
          if (xmlPullParser.isEmptyElementTag()) {
            xmlPullParser.next();
            currentDepth--;
          }
        } else if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
          currentDepth--;
        }
        builder.append(xmlPullParser.getText());
        xmlPullParser.next();
      }
      return builder.toString();
    } catch (Exception xse) {
      throw new RuntimeException(xse);
    }
  }

  public void clearCache() {
    elementTextCache = null;
  }



  @Override
  public void skipElement() {
    try {
      int currentDepth = 0;
      xmlPullParser.next(); // Skip current element
      // Process all child elements if necessary
      while(xmlPullParser.getEventType() != XmlPullParser.END_TAG || (xmlPullParser.getEventType() == XmlPullParser.END_TAG && currentDepth > 0)) {
        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
          currentDepth++;
          if (xmlPullParser.isEmptyElementTag()) {
            xmlPullParser.next();
            currentDepth--;
          }
        } else if (xmlPullParser.getEventType() == XmlPullParser.END_TAG) {
          currentDepth--;
        }
        xmlPullParser.next();
      }
    } catch (Exception xse) {
      throw new RuntimeException(xse);
    }
  }

  public String getElementNS() {
        return xmlPullParser.getNamespace();
    }
}
