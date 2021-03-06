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

import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import static dk.ott.processing.TextTrimmer.trimToNull;

public class XMLElementCursor extends BaseElementCursor {
  private XMLStreamReader2 xmlStreamReader;
  private int eventType;

  public XMLElementCursor(XMLStreamReader2 xmlStreamReader) {
    this.xmlStreamReader = xmlStreamReader;
  }

  void setEventType(int eventType) {
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
      return trimToNull(xmlStreamReader.getAttributeValue(null, attributeName));
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
      int textLength = xmlStreamReader.getTextLength();
      if (textLength > 0) {
        char[] characters = xmlStreamReader.getTextCharacters();
        int startIndex = xmlStreamReader.getTextStart();
        int endIndex = startIndex + textLength - 1;
        while(startIndex < endIndex && Character.isWhitespace(characters[startIndex])) {
          startIndex++;
        }
        while(endIndex > startIndex && Character.isWhitespace(characters[endIndex])) {
          endIndex--;
        }
        textLength = endIndex - startIndex;
        if (textLength > 0) {
          elementTextCache = new String(characters, startIndex, textLength + 1);
        }
      }
    }
    return elementTextCache;
  }

  void setText(String text) {
    this.elementTextCache = text;
  }

  public String getElementNS() {
    if (elementNamespaceCache == null) {
      elementNamespaceCache = trimToNull(xmlStreamReader.getNamespaceURI());
    }
    return elementNamespaceCache;
  }
}
