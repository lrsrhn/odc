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
package dk.ott.xml;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;

public class XmlStreamWriterWrapper implements XmlStreamBuilder {
    private static final WstxOutputFactory WSTX_OUTPUT_FACTORY = new WstxOutputFactory();
    private XMLStreamWriter2 xmlStreamWriter;
    private SwappableByteOutputStream swappableByteOutputStream;

    public XmlStreamWriterWrapper(OutputStream outputStream) {
        this.swappableByteOutputStream = new SwappableByteOutputStream(outputStream);
        createXMLStreamWriter();
    }

    private void createXMLStreamWriter() {
        try {
            xmlStreamWriter = (XMLStreamWriter2) WSTX_OUTPUT_FACTORY.createXMLStreamWriter(swappableByteOutputStream);
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
        return swappableByteOutputStream.swapOutputStream(newOutputStream);
    }
}