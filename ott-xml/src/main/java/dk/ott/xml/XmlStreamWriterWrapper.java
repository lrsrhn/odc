package dk.ott.xml;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;

public class XmlStreamWriterWrapper implements XmlStreamBuilder {
    private static final WstxOutputFactory WSTX_OUTPUT_FACTORY = new WstxOutputFactory();
    private XMLStreamWriter2 xmlStreamWriter;
    private SwappableOutputStream swappableOutputStream;

    public XmlStreamWriterWrapper(OutputStream outputStream) {
        this.swappableOutputStream = new SwappableOutputStream(outputStream);
        try {
            xmlStreamWriter = (XMLStreamWriter2) WSTX_OUTPUT_FACTORY.createXMLStreamWriter(swappableOutputStream);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public XmlStreamBuilder element(String name) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(name);
        return this;
    }

    @Override
    public XmlStreamBuilder elementShort(String name) throws XMLStreamException {
        xmlStreamWriter.writeEmptyElement(name);
        return this;
    }

    @Override
    public XmlStreamBuilder elementEnd() throws XMLStreamException {
        xmlStreamWriter.writeEndElement();
        return this;
    }

    @Override
    public XmlStreamBuilder attribute(String name, String value) throws XMLStreamException {
        xmlStreamWriter.writeAttribute(name, value);
        return this;
    }

    @Override
    public XmlStreamBuilder value(String value) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(value);
        return this;
    }

    @Override
    public XmlStreamBuilder valueNoEscaping(String value) throws XMLStreamException {
        xmlStreamWriter.writeRaw(value);
        return this;
    }

    @Override
    public void flush() throws XMLStreamException {
        xmlStreamWriter.flush();
    }

    @Override
    public XmlStreamBuilder addElement(XmlStreamBuilder simpleXmlStreamBuilder) {
        return this;
    }

    @Override
    public XmlStreamBuilder addDefaultPI() throws XMLStreamException {
        xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
        return this;
    }

    @Override
    public OutputStream swapOutputStream(OutputStream newOutputStream) {
        try {
            xmlStreamWriter.flush();
        } catch (XMLStreamException e) {
            // Eat it man!
        }
        return swappableOutputStream.swapOutputStream(newOutputStream);
    }
}