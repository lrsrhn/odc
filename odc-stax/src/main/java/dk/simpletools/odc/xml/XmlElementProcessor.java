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
package dk.simpletools.odc.xml;

import dk.simpletools.odc.core.finder.ElementFinder;
import dk.simpletools.odc.core.processing.BaseElementProcessor;
import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.StructureElement;

import javax.xml.stream.XMLStreamReader;

public class XmlElementProcessor extends BaseElementProcessor<XMLStreamReader, StructureElement> {

  public XmlElementProcessor(ElementFinder nextElementFinder, StructureElement structureElement) {
    super(nextElementFinder, structureElement);
  }

  public ObjectStore search(XMLStreamReader streamReader, StructureElement xmlElement) throws Exception {
    int currentDepth = 0;
    if (streamReader.hasNext()) {
      for (int eventType = streamReader.next(); streamReader.hasNext(); eventType = streamReader.next()) {
        switch (eventType) {
          case XMLStreamReader.START_ELEMENT:
            observablePathTraverser.startElement(xmlElement, currentDepth++);
            if (streamReader.getEventType() == XMLStreamReader.END_ELEMENT) {
              observablePathTraverser.endElement(xmlElement, --currentDepth);
            }
            break;
          case XMLStreamReader.END_ELEMENT:
            observablePathTraverser.endElement(xmlElement, --currentDepth);
            break;
        }
      }
    }
    return xmlElement.getObjectStore();
  }
}
