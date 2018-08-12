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
package dk.simpletools.odc.xpp;

import dk.simpletools.odc.core.finder.ElementFinder;
import dk.simpletools.odc.core.processing.BaseElementProcessor;
import dk.simpletools.odc.core.processing.ObjectStore;
import org.xmlpull.v1.XmlPullParser;

public class ElementProcessor extends BaseElementProcessor<XmlPullParser, XMLElement> {

  public ElementProcessor(ElementFinder nextElementFinder, XMLElement xmlElement) {
    super(nextElementFinder, xmlElement);
  }

  public ObjectStore search(XmlPullParser streamReader, XMLElement xmlElement) throws Exception {
    int currentDepth = 0;
    while (streamReader.next() != XmlPullParser.END_DOCUMENT) {
      switch (streamReader.getEventType()) {
      case XmlPullParser.START_TAG:
        observablePathTraverser.startElement(xmlElement, currentDepth++);
        if (streamReader.getEventType() == XmlPullParser.END_TAG) {
          observablePathTraverser.endElement(xmlElement, --currentDepth);
        }
        continue;
      case XmlPullParser.END_TAG:
        observablePathTraverser.endElement(xmlElement, --currentDepth);
        continue;
      }
    }
    return xmlElement.getObjectStore();
  }
}
