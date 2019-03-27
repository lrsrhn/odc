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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XPPElement implements InternalStructureElement {
  private XmlPullParser xmlPullParser;
  private String elementTextCache;
  private String elementNameCache;

  public XPPElement(XmlPullParser xmlStreamReader) {
    this.xmlPullParser = xmlStreamReader;
    this.elementNameCache = null;
  }

  public String getElementName() {
    if (elementNameCache == null) {
      elementNameCache = xmlPullParser.getName();
    }
    return elementNameCache;
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

  /**
   * NB: Only use this after all attributes have been read - Calling this will
   * move the XML parser forward
   * 
   */
  public String getText() {
    if (elementTextCache != null) {
      return elementTextCache;
    }
    try {
      if (xmlPullParser.getEventType() == XmlPullParser.TEXT && xmlPullParser.getEventType() != XmlPullParser.IGNORABLE_WHITESPACE) {
        elementTextCache = xmlPullParser.getText();
      }
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
    this.elementTextCache = null;
    this.elementNameCache = null;
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
