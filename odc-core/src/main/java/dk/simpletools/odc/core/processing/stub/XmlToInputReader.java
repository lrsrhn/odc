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
package dk.simpletools.odc.core.processing.stub;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class XmlToInputReader {

    public InputReader processXml(Reader reader) {
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
            xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
            XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(reader);
            ArrayList<Element> elements = new ArrayList<Element>(20000);
            if (streamReader.hasNext()) {
                Element currentElement = null;
                for (int eventType = streamReader.next(); streamReader.hasNext(); eventType = streamReader.next()) {
                    switch (eventType) {
                        case XMLStreamReader.START_ELEMENT:
                            currentElement = new Element(streamReader.getLocalName(), null, true);
                            elements.add(currentElement);
                            continue;
                        case XMLStreamReader.CHARACTERS:
                            if (!streamReader.isWhiteSpace()) {
                                currentElement.setTextValue(streamReader.getText());
                            }
                            continue;
                        case XMLStreamReader.END_ELEMENT:
                            elements.add(new Element(streamReader.getLocalName(), null, false));
                            continue;
                    }
                }
            }
            return new InputReader(elements);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
