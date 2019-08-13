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

import org.xmlpull.v1.XmlPullParser;

import static dk.ott.processing.TextTrimmer.trimToNull;

public class XPPElementCursor extends BaseElementCursor {
  private XmlPullParser xmlPullParser;
  private int eventType;

  public XPPElementCursor(XmlPullParser xmlStreamReader) {
    this.xmlPullParser = xmlStreamReader;
    this.elementNameCache = null;
    this.eventType = -1;
  }

  public void setEventType(int eventType) {
    this.eventType = eventType;
  }

  public String getElementName() {
    if (elementNameCache == null) {
      elementNameCache = xmlPullParser.getName();
    }
    return elementNameCache;
  }

  public String getAttributeValue(String attributeName) {
    if (eventType == XmlPullParser.START_TAG) {
      return trimToNull(xmlPullParser.getAttributeValue(null, attributeName));
    }
    return null;
  }

  public boolean hasAttribute(String attributeName) {
    if (attributeName != null) {
      for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
        if (attributeName.equals(xmlPullParser.getAttributeName(i))) {
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
    try {
      if (eventType == XmlPullParser.TEXT) {
        elementTextCache = trimToNull(xmlPullParser.getText());
      }
      return elementTextCache;
    } catch (Exception e) {
      // Element has children and does not have a value/text
      return null;
    }
  }

  public String getElementNS() {
    if (elementNamespaceCache == null) {
      elementNamespaceCache = trimToNull(xmlPullParser.getNamespace());
    }
    return elementNamespaceCache;
  }

  void setText(String text) {
    this.elementTextCache = text;
  }
}
