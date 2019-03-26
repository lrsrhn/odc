package dk.simpletools.odc.core.processing;

import dk.simpletools.odc.xml.TextExtractor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler {

    private ObjectStore objectStore;
    private SaxElement saxElement;
    private TextExtractor textExtractor;
    private ObservablePathTraverser observablePathTraverser;
    private int depth;

    public SaxHandler(ObjectStore objectStore, ObservablePathTraverser observablePathTraverser) {
        this.objectStore = objectStore;
        this.observablePathTraverser = observablePathTraverser;
        this.textExtractor = new TextExtractor();
        this.saxElement = new SaxElement(objectStore, textExtractor);
        this.depth = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            saxElement.setData(uri, localName, attributes);
            observablePathTraverser.startElement(saxElement, depth++);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        textExtractor.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            saxElement.setData(uri, localName, null);
            observablePathTraverser.text(saxElement);
            observablePathTraverser.endElement(saxElement, --depth);
            textExtractor.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
