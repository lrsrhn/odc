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
import java.io.IOException;

/**
 * 
 * Used by XmlStreamBuilder for pretty printing the xml output
 */
public class XmlPrettyPrinter {
  public static final String DEFAULT_INDENTATION = "  ";
  public static final String DEFAULT_NEWLINE = String.format("%n");
  public static final int DEFAULT_MAX_EXPECTED_XML_DEPTH = 50;

  private String[] indentations;
  private final String newline;

  public XmlPrettyPrinter(int maxXmlDepth, String indentation, String newline) {
    createIndentationArray(maxXmlDepth, indentation);
    this.newline = newline;
  }

  public XmlPrettyPrinter(int maxXmlDepth) {
    this(maxXmlDepth, DEFAULT_INDENTATION, DEFAULT_NEWLINE);
  }

  public XmlPrettyPrinter() {
    this(DEFAULT_MAX_EXPECTED_XML_DEPTH, DEFAULT_INDENTATION, DEFAULT_NEWLINE);
  }

  /**
   * Creates a list of indentations for xml depth 0 to maxXmlDepth.
   * 
   * e.g.:
   * |Xml Depth |Indentation
   * -----------------------
   * |0         |""
   * |1         |"\t"
   * |2         |"\t\t"
   * |3         |"\t\t\t"
   * ....
   * -----------------------
   * This simplifies the pretty printing process to a list lookup instead
   * of creating the correct number of indentations each time. 
   * 
   * @param maxXmlDepth
   * @param indentation
   */
  private void createIndentationArray(int maxXmlDepth, String indentation) {
    maxXmlDepth = maxXmlDepth + 1; // Add no indentation special case
    indentations = new String[maxXmlDepth];
    indentations[0] = "";
    StringBuilder builder = new StringBuilder(128);
    for (int i = 1; i < maxXmlDepth; i++) {
      builder.setLength(0); // reset
      for (int j = 0; j < i; j++) {
        builder.append(indentation);
      }
      indentations[i] = builder.toString();
    }
  }

  public void appendIndentation(Appendable appendable, int depth) throws IOException {
    if (depth > 0 && indentations.length > depth) {
      appendable.append(indentations[depth]);
    }
  }

  public void appendIndentation(XmlStreamBuilder xmlStreamBuilder, int depth) throws XMLStreamException {
    if (depth > 0 && indentations.length > depth) {
      xmlStreamBuilder.valueNoEscaping(indentations[depth]);
    }
  }

  public void appendNewline(Appendable appendable) throws IOException {
    appendable.append(newline);
  }

  public void appendNewline(XmlStreamBuilder xmlStreamBuilder) throws XMLStreamException {
    xmlStreamBuilder.valueNoEscaping(newline);
  }
}
