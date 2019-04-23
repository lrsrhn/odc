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

import dk.ott.core.processing.DomElement;
import dk.ott.core.processing.DomNodeProcessor;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.ObservableTree;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Reader;

public class DomObservableTree extends ObservableTree {

  private DocumentBuilderFactory documentBuilderFactory;

  public DomObservableTree() {
    documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      documentBuilderFactory.setCoalescing(true);
      documentBuilderFactory.setIgnoringElementContentWhitespace(true);
      documentBuilderFactory.setIgnoringComments(true);
  }

  @Override
  public ObjectStore find(Reader reader, ObjectStore objectStore) {
    try {
      return find(documentBuilderFactory.newDocumentBuilder().parse(new InputSource(reader)), objectStore);
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }
  }

  public ObjectStore find(Document document) {
    return find(document, new ObjectStore());
  }

  public ObjectStore find(Document document, ObjectStore objectStore) {
    try {
      DomNodeProcessor domNodeProcessor = new DomNodeProcessor(rootElementFinder.getElementFinder(), objectStore);
      DomElement domElement = new DomElement();
      return domNodeProcessor.search(document, domElement);
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }
  }
}