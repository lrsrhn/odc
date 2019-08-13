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

import dk.ott.core.Node;
import dk.ott.xml.XmlRawTextReader2;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public final class XmlElementProcessor extends BaseElementProcessor<XMLStreamReader2, XMLElementCursor> {

    private XmlRawTextReader2 xmlRawTextReader;

    public XmlElementProcessor(Node nextNode, ObjectStore objectStore, XmlRawTextReader2 xmlRawTextReader) {
        super(nextNode, objectStore);
        this.xmlRawTextReader = xmlRawTextReader;
    }

    public ObjectStore search(XMLStreamReader2 streamReader, XMLElementCursor xmlElement) throws Exception {
        int currentDepth = 0;
        while (streamReader.hasNext() && !xmlElement.mustStopProcessing()) {
            int eventType = streamReader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    xmlElement.setEventType(eventType);
                    switch (observableTreeTraverser.startElement(xmlElement, currentDepth++)) {
                        case SKIP_ELEMENT:
                            streamReader.skipElement();
                            xmlElement.setEventType(XMLStreamReader.END_ELEMENT);
                            observableTreeTraverser.endElement(xmlElement, --currentDepth);
                            continue;
                        case READ_RAW_TEXT:
                            if (xmlRawTextReader == null) {
                                throw new IllegalStateException("Raw text read disabled - please enable");
                            }
                            // Read raw text until end of element
                            xmlRawTextReader.setStartIndex(streamReader.getLocation().getCharacterOffset());
                            streamReader.skipElement();
                            xmlElement.setText(xmlRawTextReader.readRawText(streamReader.getLocation().getCharacterOffset()));
                            // Notify OnTextHandler
                            observableTreeTraverser.text(xmlElement);
                            // Notify OnEndHandler
                            xmlElement.setEventType(XMLStreamReader.END_ELEMENT);
                            observableTreeTraverser.endElement(xmlElement, --currentDepth);
                            continue;
                    }
                    continue;
                case XMLStreamConstants.CHARACTERS:
                    if (!streamReader.isWhiteSpace()) {
                        xmlElement.setEventType(eventType);
                        observableTreeTraverser.text(xmlElement);
                    }
                    continue;
                case XMLStreamReader.END_ELEMENT:
                    xmlElement.setEventType(eventType);
                    observableTreeTraverser.endElement(xmlElement, --currentDepth);
            }
        }
        return objectStore;
    }
}
