package dk.ott.xml;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class XmlStreamBuilderToString implements XmlStreamBuilder {
    private XmlStreamBuilder xmlStreamBuilder;
    private ByteArrayOutputStream byteArrayOutputStream;

    public XmlStreamBuilderToString(XmlStreamBuilder xmlStreamBuilder, ByteArrayOutputStream byteArrayOutputStream) {
        this.xmlStreamBuilder = xmlStreamBuilder;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    @Override
    public XmlStreamBuilder element(String name) throws XMLStreamException {
        xmlStreamBuilder.element(name);
        return this;
    }

    @Override
    public XmlStreamBuilder elementShort(String name) throws XMLStreamException {
        xmlStreamBuilder.elementShort(name);
        return this;
    }

    @Override
    public XmlStreamBuilder elementEnd() throws XMLStreamException {
        xmlStreamBuilder.elementEnd();
        return this;
    }

    @Override
    public XmlStreamBuilder attribute(String name, String value) throws XMLStreamException {
        xmlStreamBuilder.attribute(name, value);
        return this;
    }

    @Override
    public XmlStreamBuilder value(String value) throws XMLStreamException {
        xmlStreamBuilder.value(value);
        return this;
    }

    @Override
    public XmlStreamBuilder valueNoEscaping(String value) throws XMLStreamException {
        xmlStreamBuilder.valueNoEscaping(value);
        return this;
    }

    @Override
    public void flush() throws XMLStreamException {
        xmlStreamBuilder.flush();
    }

    @Override
    public XmlStreamBuilder addElement(XmlStreamBuilder simpleXmlStreamBuilder) {
        return this;
    }

    @Override
    public XmlStreamBuilder addDefaultPI() throws XMLStreamException {
        xmlStreamBuilder.addDefaultPI();
        return this;
    }

    @Override
    public OutputStream swapOutputStream(OutputStream newOutputStream) {
        return xmlStreamBuilder.swapOutputStream(newOutputStream);
    }

    public String toString() {
        try {
            xmlStreamBuilder.flush();
            return byteArrayOutputStream.toString();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
