package dk.ott.core.processing;

import dk.ott.xml.TextExtractor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler {
    private SaxElement saxElement;
    private TextExtractor textExtractor;
    private ObservableTreeTraverser observableTreeTraverser;
    private XMLReader xmlReader;
    private SaxElementSkippingHandler saxElementSkippingHandler;
    private int depth;

    public SaxHandler(XMLReader xmlReader, ObservableTreeTraverser observableTreeTraverser, SaxElementSkippingHandler saxElementSkippingHandler) {
        this.observableTreeTraverser = observableTreeTraverser;
        this.textExtractor = new TextExtractor();
        saxElementSkippingHandler.setSaxHandler(this);
        this.saxElement = new SaxElement(textExtractor);
        this.xmlReader = xmlReader;
        this.saxElementSkippingHandler = saxElementSkippingHandler;
        this.depth = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            saxElement.setData(uri, localName, attributes);
            if (observableTreeTraverser.startElement(saxElement, depth++)) {
                saxElementSkippingHandler.resetDepth();
                xmlReader.setContentHandler(saxElementSkippingHandler);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (observableTreeTraverser.isTextHandlerSet()) {
            textExtractor.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            saxElement.setData(uri, localName, null);
            observableTreeTraverser.text(saxElement);
            observableTreeTraverser.endElement(saxElement, --depth);
            textExtractor.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
