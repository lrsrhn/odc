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
package dk.simpletools.odc.xpp;

import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.ObservablePathFinder;
import dk.simpletools.odc.core.processing.XPPElement;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Reader;

public class XppPathFinder extends ObservablePathFinder {
  private static final XmlPullParserFactory DEFAULT_XML_PULL_PARSER_FACTORY = createFactory();

  private XmlPullParserFactory xmlPullParserFactory;

  static XmlPullParserFactory createFactory() {
    try {
      XmlPullParserFactory factory  = XmlPullParserFactory.newInstance();
      factory.setNamespaceAware(true);
      return factory;
    } catch (XmlPullParserException e) {
      throw new RuntimeException(e);
    }
  }

  public XppPathFinder(XmlPullParserFactory xmlPullParserFactory) {
    super();
    this.xmlPullParserFactory = xmlPullParserFactory;
  }

  public XppPathFinder() {
    this(DEFAULT_XML_PULL_PARSER_FACTORY);
  }

  @Override
  public ObjectStore find(Reader reader, ObjectStore objectStore) {
    XmlPullParser streamReader = null;
    try {
      streamReader = xmlPullParserFactory.newPullParser();
      streamReader.setInput(reader);
      XPPElement XPPElement = new XPPElement(streamReader);
      ElementProcessor elementProcessor = new ElementProcessor(rootElementFinder.getElementFinder(), XPPElement);
      return elementProcessor.search(streamReader, XPPElement);
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    } finally {
      if (streamReader != null) {
        try {
          reader.close();
        } catch (Exception ex) {
          // Ignore
        }
      }
    }
  }
}