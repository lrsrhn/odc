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

public class NaiveXmlEscaper {
  /**
   * Performs naive escaping of xml.
   */
  public static void escapeAndInsert(Appendable output, final String text) throws IOException {
    int startIndex = 0;
    int endIndex = 0;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      switch (c) {
      case '<':
        output.append(text, startIndex, endIndex);
        output.append("&lt;");
        endIndex = startIndex = i + 1;
        break;
      case '>':
        output.append(text, startIndex, endIndex);
        output.append("&gt;");
        endIndex = startIndex = i + 1;
        break;
      case '\"':
        output.append(text, startIndex, endIndex);
        output.append("&quot;");
        endIndex = startIndex = i + 1;
        break;
      case '&':
        output.append(text, startIndex, endIndex);
        output.append("&amp;");
        endIndex = startIndex = i + 1;
        break;
      case '\'':
        output.append(text, startIndex, endIndex);
        output.append("&apos;");
        endIndex = startIndex = i + 1;
        break;
      default:
        endIndex++;
      }
    }
    if (startIndex == 0 && endIndex == text.length()) {
      output.append(text);
    } else {
      output.append(text, startIndex, endIndex);
    }
  }
}
