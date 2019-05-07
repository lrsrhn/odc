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
            if (observableTreeTraverser.startElement(saxElement, depth++) == EventAction.SKIP_ELEMENT) {
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
