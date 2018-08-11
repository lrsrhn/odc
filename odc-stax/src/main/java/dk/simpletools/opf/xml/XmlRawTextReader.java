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
package dk.simpletools.opf.xml;

import java.io.IOException;
import java.io.Reader;

public class XmlRawTextReader extends Reader {
  private Reader reader;
  private TextExtractor textExtractor;

  public XmlRawTextReader(Reader reader) {
    this.reader = reader;
    this.textExtractor = new TextExtractor();
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    textExtractor.preRead(cbuf ,len);
    int result = reader.read(cbuf, off, len);
    textExtractor.postRead(result);
    return result;
  }

  @Override
  public int read() throws IOException {
    int result = super.read();
    textExtractor.postRead(result);
    return result;
  }

  @Override
  public int read(char[] cbuf) throws IOException {
    textExtractor.preRead(cbuf, 0);
    int result = super.read(cbuf);
    textExtractor.postRead(result);
    return result;
  }

  @Override
  public void close() throws IOException {
    textExtractor = null;
    reader.close();
  }

  public void setStartIndex(int startIndex) { textExtractor.setStartIndex(startIndex);}

  public String readRawText(int endIndex) {
    return textExtractor.readRawText(endIndex);
  }

  private static class TextExtractor {
    private StringBuilder builderBuffer;
    private char[] cbuf;
    private int currentEndOffset;
    private int currentStartOffset;
    private int startIndex;

    public TextExtractor() {
      this.currentEndOffset = 0;
      this.currentStartOffset = 0;
      this.startIndex = -1;
      builderBuffer = new StringBuilder(128);
    }

    public void preRead(char[] cbuf, int len) {
      this.cbuf = cbuf;
      if (startIndex != -1) {
        int startIndexRelativeToOffset = startIndex - currentStartOffset;
        builderBuffer.append(cbuf, startIndexRelativeToOffset, len - startIndexRelativeToOffset);
      }
    }

    public void postRead(int result) {
      currentStartOffset = currentEndOffset;
      currentEndOffset += result;
    }

    public void setStartIndex(int startIndex) {
      this.startIndex = startIndex;
    }

    public String readRawText(int endIndex) {
      endIndex = findProperEndIndex(Math.abs(currentStartOffset - endIndex));
      if (endIndex >= 0) {
        // The element end tag is in the cbuf array
        startIndex = startIndex < currentStartOffset ? 0 : startIndex - currentStartOffset;
        if (startIndex >= endIndex) {
          // Occurs with <test/> and <test></test>
          return "";
        }
        builderBuffer.append(cbuf, startIndex, endIndex - startIndex);
      } else {
        // The element end tag is already in the StringBuilder
        endIndex = builderBuffer.length() - 1;
        while(builderBuffer.charAt(endIndex) != '<') {
          endIndex--;
        }
        builderBuffer.setLength(endIndex);
      }
      String rawText = builderBuffer.toString();
      builderBuffer.setLength(0);
      startIndex = -1;
      return rawText;
    }

    private int findProperEndIndex(int endIndex) {
      endIndex--;
      while (endIndex >= 0 && cbuf[endIndex] != '<') {
        endIndex--;
      }
      return endIndex;
    }
  }
}
