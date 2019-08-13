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
package dk.ott.processing;

import com.ctc.wstx.stax.WstxInputFactory;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;

import java.io.Reader;

public class StaxObservableTree extends ObservableTree {
  private static final WstxInputFactory DEFAULT_XML_INPUT_FACTORY = createDefaultFactory();

  static WstxInputFactory createDefaultFactory() {
    WstxInputFactory xmlInputFactory = new WstxInputFactory();
    xmlInputFactory.configureForConvenience();
    xmlInputFactory.setProperty(WstxInputFactory.P_INTERN_NAMES, false);
    xmlInputFactory.setProperty(WstxInputFactory.P_INTERN_NS_URIS, false);
    return xmlInputFactory;
  }

  private WstxInputFactory xmlInputFactory;
  private boolean isRawTextReadingEnabled;
  private XMLValidationSchema xmlValidationSchema;

  public StaxObservableTree() {
    this(DEFAULT_XML_INPUT_FACTORY);
    this.isRawTextReadingEnabled = true;
  }

  public StaxObservableTree(WstxInputFactory xmlInputFactory) {
    if (xmlInputFactory != DEFAULT_XML_INPUT_FACTORY) {
      xmlInputFactory.configureForConvenience();
      xmlInputFactory.setProperty(WstxInputFactory.P_INTERN_NAMES, false);
      xmlInputFactory.setProperty(WstxInputFactory.P_INTERN_NS_URIS, false);
    }
    this.xmlInputFactory = xmlInputFactory;
  }

  public void enableRawTextReading() {
    this.isRawTextReadingEnabled = true;
  }

  public void disableRawTextReading() {
    this.isRawTextReadingEnabled = false;
  }

  public XMLValidationSchema getXmlValidationSchema() {
    return xmlValidationSchema;
  }

  public void setXmlValidationSchema(XMLValidationSchema xmlValidationSchema) {
    this.xmlValidationSchema = xmlValidationSchema;
  }

  @Override
  public ObjectStore find(Reader reader, ObjectStore objectStore) {
    XMLStreamReader2 streamReader = null;
    XmlRawTextReader2 xmlRawTextReader = null;
    try {
      if (isRawTextReadingEnabled) {
        xmlRawTextReader = new XmlRawTextReader2(reader);
        streamReader = (XMLStreamReader2) xmlInputFactory.createXMLStreamReader(xmlRawTextReader);
      } else {
        streamReader = (XMLStreamReader2) xmlInputFactory.createXMLStreamReader(reader);
      }
      if (xmlValidationSchema != null) {
        streamReader.validateAgainst(xmlValidationSchema);
      }
      XMLElementCursor xmlElement = new XMLElementCursor(streamReader);
      XmlElementProcessor xmlElementProcessor = new XmlElementProcessor(rootEdgeReference.getElementFinder(), objectStore, xmlRawTextReader);
      return xmlElementProcessor.search(streamReader, xmlElement);
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