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

import dk.ott.core.processing.*;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.Reader;

public class SaxObservableTree extends ObservableTree {
  private static final SAXParserFactory DEFAULT_XML_INPUT_FACTORY = createDefaultFactory();

  static SAXParserFactory createDefaultFactory() {
      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      return saxParserFactory;
  }

  private SAXParserFactory saxParserFactory;

  public SaxObservableTree() {
    this(DEFAULT_XML_INPUT_FACTORY);
  }

  public SaxObservableTree(SAXParserFactory saxParserFactory) {
    if (saxParserFactory != DEFAULT_XML_INPUT_FACTORY) {
    }
    saxParserFactory.setNamespaceAware(true);
    this.saxParserFactory = saxParserFactory;
  }

  @Override
  public ObjectStore find(Reader reader, ObjectStore objectStore) {
    try {
      SAXParser saxParser = saxParserFactory.newSAXParser();
      XMLReader xmlReader = saxParser.getXMLReader();
      SaxElementSkippingHandler saxElementSkippingHandler = new SaxElementSkippingHandler(xmlReader);
      SaxHandler saxHandler = new SaxHandler(xmlReader, new ObservableTreeTraverser(rootTreeEdgeReference.getElementFinder(), objectStore), saxElementSkippingHandler);
      xmlReader.setContentHandler(saxHandler);
      xmlReader.parse(new InputSource(reader));
      return objectStore;
    } catch (StopProcessingException ex) {
      return objectStore;
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }
  }
}