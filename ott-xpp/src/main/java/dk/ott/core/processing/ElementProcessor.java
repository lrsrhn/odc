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

import dk.ott.core.finder.ElementFinder;
import org.xmlpull.v1.XmlPullParser;

public class ElementProcessor extends BaseElementProcessor<XmlPullParser, XPPElementCursor> {

  public ElementProcessor(ElementFinder nextElementFinder, ObjectStore objectStore) {
    super(nextElementFinder, objectStore);
  }

  public ObjectStore search(XmlPullParser streamReader, XPPElementCursor xppElement) throws Exception {
    int currentDepth = 0;
    boolean continueLoop = true;
    while (continueLoop && !xppElement.mustStopProcessing()) {
        int eventType = streamReader.next();
          switch (eventType) {
              case XmlPullParser.START_TAG:
                  xppElement.setEventType(eventType);
                  switch (observableTreeTraverser.startElement(xppElement, currentDepth++)) {
                      case SKIP_ELEMENT:
                          skipElement(streamReader);
                          xppElement.setEventType(XmlPullParser.END_TAG);
                          observableTreeTraverser.endElement(xppElement, --currentDepth);
                          continue;
                      case READ_RAW_TEXT:
                          // Read raw and set on element
                          xppElement.setText(readRaw(streamReader));
                          observableTreeTraverser.text(xppElement);
                          // Handle end element
                          xppElement.setEventType(XmlPullParser.END_TAG);
                          observableTreeTraverser.endElement(xppElement, --currentDepth);
                          continue;
                  }
                  continue;
              case XmlPullParser.TEXT:
                  if (!streamReader.isWhitespace()) {
                      xppElement.setEventType(eventType);
                      observableTreeTraverser.text(xppElement);
                  }
                  continue;
              case XmlPullParser.END_TAG:
                  xppElement.setEventType(eventType);
                  observableTreeTraverser.endElement(xppElement, --currentDepth);
                  continue;
              case XmlPullParser.END_DOCUMENT:
                  continueLoop = false;
          }
      }
    return objectStore;
  }

  private void skipElement(XmlPullParser xmlPullParser) {
    try {
      int currentDepth = 0;
      int eventType = xmlPullParser.next(); // Skip current element
      // Process all child elements if necessary
      while(eventType != XmlPullParser.END_TAG || currentDepth > 0) {
        if (eventType == XmlPullParser.START_TAG) {
          currentDepth++;
          if (xmlPullParser.isEmptyElementTag()) {
            xmlPullParser.next();
            currentDepth--;
          }
        } else if (eventType == XmlPullParser.END_TAG) {
          currentDepth--;
        }
        eventType = xmlPullParser.next();
      }
    } catch (Exception xse) {
      throw new RuntimeException(xse);
    }
  }

    private String readRaw(XmlPullParser xmlPullParser) {
    try {
      StringBuilder builder = new StringBuilder(128);
      int currentDepth = 0;
      int eventType = xmlPullParser.next(); // Skip current element
      // Process all child elements if necessary
      while(eventType != XmlPullParser.END_TAG || currentDepth > 0) {
        if (eventType == XmlPullParser.START_TAG) {
          currentDepth++;
          if (xmlPullParser.isEmptyElementTag()) {
            xmlPullParser.next();
            currentDepth--;
          }
        } else if (eventType == XmlPullParser.END_TAG) {
          currentDepth--;
        }
        builder.append(xmlPullParser.getText());
        eventType = xmlPullParser.next();
      }
      return builder.toString();
    } catch (Exception xse) {
      throw new RuntimeException(xse);
    }
  }
}
