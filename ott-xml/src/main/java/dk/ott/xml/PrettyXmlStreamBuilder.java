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

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;

/**
 * Very simple xml builder with no error checking
 *
 * This class is not responsible for flushing or closing the outputStream
 */
public class PrettyXmlStreamBuilder implements XmlStreamBuilder {
    private int depth;
    private XmlState state;
    private XmlStreamBuilder xmlStreamWriter;
    private XmlPrettyPrinter xmlPrettyPrinter;

    public PrettyXmlStreamBuilder(XmlStreamBuilder xmlStreamWriter) {
        this.xmlStreamWriter = xmlStreamWriter;
        this.depth = 0;
        this.xmlPrettyPrinter = new XmlPrettyPrinter();
        state = XmlState.BEGIN_DOCUMENT;
    }

    @Override
    public PrettyXmlStreamBuilder element(String name) throws XMLStreamException {
        if (state == XmlState.WRITING_TEXT) {
            throw new RuntimeException("Mixed content not supported - please use 'valueNoEscaping'");
        }
        if (state != XmlState.BEGIN_DOCUMENT) {
            newLineIfPrettyPrint();
        }
        indentIfPrettyPrinter(depth++);
        xmlStreamWriter.element(name);
        state = XmlState.START_ELEMENT;
        return this;
    }

    @Override
    public PrettyXmlStreamBuilder elementShort(String name) throws XMLStreamException {
        if (state == XmlState.WRITING_TEXT) {
            throw new RuntimeException("Mixed content not supported - please use 'valueNoEscaping'");
        }
        newLineIfPrettyPrint();
        indentIfPrettyPrinter(depth);
        xmlStreamWriter.elementShort(name);
        state = XmlState.START_ELEMENT;
        return this;
    }

    @Override
    public PrettyXmlStreamBuilder elementEnd() throws XMLStreamException {
        depth--;
        if (state == XmlState.WRITING_TEXT) {
            xmlStreamWriter.elementEnd();
        } else {
            newLineIfPrettyPrint();
            indentIfPrettyPrinter(depth);
            xmlStreamWriter.elementEnd();
        }
        state = XmlState.END_ELEMENT;
        return this;
    }

    @Override
    public PrettyXmlStreamBuilder attribute(String name, String value) throws XMLStreamException {
        if (state == XmlState.WRITING_TEXT || state == XmlState.END_ELEMENT) {
            throw new RuntimeException("Only possible to write attributes at start element");
        }
        state = XmlState.WRITING_ATTRIBUTES;
        xmlStreamWriter.attribute(name, value);
        return this;
    }

    @Override
    public PrettyXmlStreamBuilder value(String value) throws XMLStreamException {
        if (state == XmlState.WRITING_TEXT || state == XmlState.END_ELEMENT) {
            throw new RuntimeException("Only possible to write text at start element");
        }
        state = XmlState.WRITING_TEXT;
        xmlStreamWriter.value(value);
        return this;
    }

    @Override
    public PrettyXmlStreamBuilder valueNoEscaping(String value) throws XMLStreamException {
        if (state == XmlState.WRITING_TEXT || state == XmlState.END_ELEMENT) {
            throw new RuntimeException("Only possible to write text at start element");
        }
        state = XmlState.WRITING_TEXT;
        xmlStreamWriter.valueNoEscaping(value);
        return this;
    }

    private void newLineIfPrettyPrint() throws XMLStreamException {
        if (xmlPrettyPrinter != null) {
            xmlPrettyPrinter.appendNewline(xmlStreamWriter);
        }
    }

    private void indentIfPrettyPrinter(int depth) throws XMLStreamException {
        if (xmlPrettyPrinter != null) {
            xmlPrettyPrinter.appendIndentation(xmlStreamWriter, depth);
        }
    }

    @Override
    public void flush() throws XMLStreamException {
        xmlStreamWriter.flush();
    }

    /**
     *
     * Usage helps IDE autoformatter to provide correct indentation of chained
     * method
     * call. The indentation follows the normal XML pretty print indentation.
     *
     * @param simpleXmlStreamBuilder
     * @return
     */
    @Override
    public XmlStreamBuilder addElement(XmlStreamBuilder simpleXmlStreamBuilder) {
        return this;
    }

    @Override
    public PrettyXmlStreamBuilder addDefaultPI() throws XMLStreamException {
        if (state != XmlState.BEGIN_DOCUMENT) {
            throw new RuntimeException("Setting default PI must be the first thing to call");
        }
        xmlStreamWriter.addDefaultPI();
        newLineIfPrettyPrint();
        return this;
    }

    @Override
    public OutputStream swapOutputStream(OutputStream newOutputStream) {
        return xmlStreamWriter.swapOutputStream(newOutputStream);
    }

    private enum XmlState {
        BEGIN_DOCUMENT,
        WRITING_ATTRIBUTES,
        WRITING_TEXT,
        END_ELEMENT,
        START_ELEMENT
    }
}