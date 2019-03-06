/**
 * The MIT License
 * Copyright © 2018 Lars Storm
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dk.simpletools.odc.core.processing;

import dk.simpletools.odc.core.finder.ElementFinder;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public final class XmlElementProcessor extends BaseElementProcessor<XMLStreamReader, XMLElement> {

    public XmlElementProcessor(ElementFinder nextElementFinder, XMLElement xmlElement) {
        super(nextElementFinder, xmlElement);
    }

    public ObjectStore search(XMLStreamReader streamReader, XMLElement xmlElement) throws Exception {
        int currentDepth = 0;
        while (streamReader.hasNext()) {
            switch (streamReader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    observablePathTraverser.startElement(xmlElement, currentDepth++);
                    // Traverser may have called skipElement
                    if (streamReader.getEventType() == XMLStreamConstants.END_ELEMENT) {
                        observablePathTraverser.endElement(xmlElement, --currentDepth);
                    }
                    continue;
                case XMLStreamConstants.CHARACTERS:
                    observablePathTraverser.text(xmlElement);
                    continue;
                case XMLStreamReader.END_ELEMENT:
                    observablePathTraverser.endElement(xmlElement, --currentDepth);
            }
        }
        return xmlElement.getObjectStore();
    }
}
