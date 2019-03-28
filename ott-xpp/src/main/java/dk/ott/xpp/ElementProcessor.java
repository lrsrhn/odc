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
package dk.ott.xpp;

import dk.ott.core.finder.ElementFinder;
import dk.ott.core.processing.BaseElementProcessor;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.XPPElement;
import org.xmlpull.v1.XmlPullParser;

public class ElementProcessor extends BaseElementProcessor<XmlPullParser, XPPElement> {

  public ElementProcessor(ElementFinder nextElementFinder, ObjectStore objectStore) {
    super(nextElementFinder, objectStore);
  }

  public ObjectStore search(XmlPullParser streamReader, XPPElement XPPElement) throws Exception {
    int currentDepth = 0;
    boolean continueLoop = true;
    while (continueLoop) {
          switch (streamReader.next()) {
              case XmlPullParser.START_TAG:
                  observableTreeTraverser.startElement(XPPElement, currentDepth++);
                  if (streamReader.getEventType() == XmlPullParser.END_TAG) {
                      observableTreeTraverser.endElement(XPPElement, --currentDepth);
                  }
                  continue;
              case XmlPullParser.TEXT:
                  observableTreeTraverser.text(XPPElement);
                  continue;
              case XmlPullParser.END_TAG:
                  observableTreeTraverser.endElement(XPPElement, --currentDepth);
                  continue;
              case XmlPullParser.END_DOCUMENT:
                  continueLoop = false;
          }
      }
    return objectStore;
  }
}
