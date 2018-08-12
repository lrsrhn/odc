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
package dk.simpletools.odc.xml;

import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.ObservablePathFinder;
import dk.simpletools.odc.core.processing.StructureElement;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;

public class StaxPathFinder extends ObservablePathFinder {
  private static final XMLInputFactory DEFAULT_XML_INPUT_FACTORY = createDefaultFactory();

  static XMLInputFactory createDefaultFactory() {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
    xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);

    return xmlInputFactory;
  }

  private XMLInputFactory xmlInputFactory;

  public StaxPathFinder() {
    this(DEFAULT_XML_INPUT_FACTORY);
  }

  public StaxPathFinder(XMLInputFactory xmlInputFactory) {
    if (xmlInputFactory != DEFAULT_XML_INPUT_FACTORY) {
      xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
    }
    this.xmlInputFactory = xmlInputFactory;
  }

  public ObjectStore find(Reader reader) {
    XMLStreamReader streamReader = null;
    try {
      XmlRawTextReader xmlRawTextReader = new XmlRawTextReader(reader);
      streamReader = xmlInputFactory.createXMLStreamReader(xmlRawTextReader);
      StructureElement structureElement = new XMLElement(streamReader, xmlRawTextReader);
      XmlElementProcessor xmlElementProcessor = new XmlElementProcessor(rootElementFinder.getElementFinder(), structureElement);
      return xmlElementProcessor.search(streamReader, structureElement);
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    } finally {
      if (streamReader != null) {
        try {
          streamReader.close();
        } catch (Exception ex) {
          // Ignore
        }
      }
    }
  }
}