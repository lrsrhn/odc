package dk.ott.xml;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;

public interface XmlStreamBuilder<T extends XmlStreamBuilder> {
    T element(String name) throws XMLStreamException;

    T elementShort(String name) throws XMLStreamException;

    T elementEnd() throws XMLStreamException;

    T attribute(String name, String value) throws XMLStreamException;

    T value(String value) throws XMLStreamException;

    T valueNoEscaping(String value) throws XMLStreamException;

    void flush() throws XMLStreamException;

    T addElement(XmlStreamBuilder simpleXmlStreamBuilder);

    T addDefaultPI() throws XMLStreamException;

    /**
     * Replace the current outputstream with another one.
     * Flush is called on the XMLStreamWriter before the swap is performed.
     *
     * The return value is the old OutputStream
     */
    OutputStream swapOutputStream(OutputStream newOutputStream);
}
