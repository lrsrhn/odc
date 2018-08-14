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
