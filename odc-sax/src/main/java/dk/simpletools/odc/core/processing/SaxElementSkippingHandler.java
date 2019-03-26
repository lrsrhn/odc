package dk.simpletools.odc.core.processing;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SaxElementSkippingHandler extends DefaultHandler {
    private int depth;
    private SaxHandler saxHandler;
    private XMLReader xmlReader;

    public SaxElementSkippingHandler(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
        resetDepth();
    }

    public void setSaxHandler(SaxHandler saxHandler) {
        this.saxHandler = saxHandler;
    }

    public void resetDepth() {
        depth = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        depth++;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (depth == 0) {
            xmlReader.setContentHandler(saxHandler);
            saxHandler.endElement(uri, localName, qName);
        }
        depth--;
    }
}
