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
package dk.simpletools.odc.core.xml.builder;

import java.io.IOException;
import java.util.Arrays;

/**
 * Very simple xml builder with no error checking
 * 
 * This class is not responsible for flushing or closing the outputStream
 */
public class XmlStreamBuilder {
  private StringStack elementNameStack;
  private Appendable outputStream;
  private XmlState state;
  private XmlPrettyPrinter xmlPrettyPrinter;

  public XmlStreamBuilder(Appendable outputStream) {
    this(outputStream, null);
  }

  public XmlStreamBuilder(Appendable outputStream, XmlPrettyPrinter xmlPrettyPrinter) {
    elementNameStack = new StringStack();
    state = XmlState.BEGIN_DOCUMENT;
    this.outputStream = outputStream;
    this.xmlPrettyPrinter = xmlPrettyPrinter;
  }

  public XmlStreamBuilder element(String name) throws IOException {
    checkNull(name, "The element name is missing");
    if (state == XmlState.START_ELEMENT || state == XmlState.WRITING_ATTRIBUTES) {
      outputStream.append('>');
      newLineIfPrettyPrint();
    } else if (state == XmlState.END_ELEMENT) {
      newLineIfPrettyPrint();
    }
    indentIfPrettyPrinter(elementNameStack.size());
    elementNameStack.push(name);
    outputStream.append('<').append(name);
    state = XmlState.START_ELEMENT;
    return this;
  }

  public XmlStreamBuilder elementEnd() throws IOException {
    if (state == XmlState.START_ELEMENT || state == XmlState.WRITING_ATTRIBUTES) {
      outputStream.append(">");
      newLineIfPrettyPrint();
      indentIfPrettyPrinter(elementNameStack.size() - 1);
      outputStream.append("</").append(elementNameStack.pop()).append('>');
    } else if (state == XmlState.END_ELEMENT) {
      newLineIfPrettyPrint();
      indentIfPrettyPrinter(elementNameStack.size() - 1);
      outputStream.append("</").append(elementNameStack.pop()).append('>');
    } else {
      outputStream.append("</").append(elementNameStack.pop()).append('>');
    }
    state = XmlState.END_ELEMENT;
    return this;
  }

  private static void checkNull(String text, String message) {
    if (text == null || text.trim().equalsIgnoreCase("null")) {
      throw new IllegalArgumentException(message);
    }
  }

  public XmlStreamBuilder elementShortEnd() throws IOException {
    outputStream.append("/>");
    elementNameStack.pop();
    state = XmlState.END_ELEMENT;
    return this;
  }

  public XmlStreamBuilder attribute(String name, String value) throws IOException {
    checkNull(name, "The attribute name is missing");
    checkNull(value, "The attribute value is missing for attribute: " + name);
    state = XmlState.WRITING_ATTRIBUTES;
    outputStream.append(' ').append(name).append("=\"");
    NaiveXmlEscaper.escapeAndInsert(outputStream, value);
    outputStream.append("\"");
    return this;
  }

  public XmlStreamBuilder value(String value) throws IOException {
    checkNull(value, "The value for the element [" + elementNameStack.peek() + "] is missing");
    state = XmlState.WRITING_TEXT;
    outputStream.append('>');
    NaiveXmlEscaper.escapeAndInsert(outputStream, value);
    return this;
  }

  public XmlStreamBuilder valueNoEscaping(String value) throws IOException {
    checkNull(value, "The value for the element [" + elementNameStack.peek() + "] is missing");
    state = XmlState.WRITING_TEXT;
    outputStream.append('>').append(value);
    return this;
  }

  private void newLineIfPrettyPrint() throws IOException {
    if (xmlPrettyPrinter != null) {
      xmlPrettyPrinter.appendNewline(outputStream);
    }
  }

  private void indentIfPrettyPrinter(int depth) throws IOException {
    if (xmlPrettyPrinter != null) {
      xmlPrettyPrinter.appendIndentation(outputStream, depth);
    }
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
  public XmlStreamBuilder addElement(XmlStreamBuilder simpleXmlStreamBuilder) {
    return this;
  }

  public void addDefaultPI() throws IOException {
    outputStream.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
    newLineIfPrettyPrint();
  }

  public void addCustomPI(String pi) throws IOException {
    outputStream.append(pi);
    newLineIfPrettyPrint();
  }

  private enum XmlState {
    BEGIN_DOCUMENT,
    WRITING_ATTRIBUTES,
    WRITING_TEXT,
    END_ELEMENT,
    START_ELEMENT
  }

  private final class StringStack {
    private String[] array;
    private int head;

    StringStack() {
      this.array = new String[16];
      this.head = -1;
    }

    String pop() {
      if (head != -1) {
        String value = array[head];
        array[head--] = null;
        return value;
      }
      throw new ArrayIndexOutOfBoundsException();
    }

    void push(String value) {
      array[++head] = value;
      if (head == array.length - 1) {
        array = Arrays.copyOf(array, array.length * 2);
      }
    }

    String peek() {
      return head != -1 ? array[head] : null;
    }

    int size() {
      return head + 1;
    }
  }
}