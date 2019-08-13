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

import java.io.IOException;
import java.io.Reader;

/**
 * @deprecated It seems that Woodstox makes changes to it's input buffer while processing. Hence the output of this
 * class is unreliable. This implementation is faster than  XmlRawTextReader2 though.
 */
public class XmlRawTextReader extends Reader {
  private Reader reader;
  private TextExtractor textExtractor;

  public XmlRawTextReader(Reader reader) {
    this.reader = reader;
    this.textExtractor = new TextExtractor();
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    textExtractor.preRead(cbuf);
    int result = reader.read(cbuf, off, len);
    textExtractor.postRead(result, off);
    return result;
  }

  @Override
  public int read() throws IOException {
    int result = super.read();
    textExtractor.postRead(result, 0);
    return result;
  }

  @Override
  public int read(char[] cbuf) throws IOException {
    textExtractor.preRead(cbuf);
    int result = super.read(cbuf);
    textExtractor.postRead(result, 0);
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
    private int previousOffset;
    private int previousLength;
    private int startIndex;

    public TextExtractor() {
      this.currentEndOffset = 0;
      this.currentStartOffset = 0;
      this.previousOffset = 0;
      this.previousLength = 0;
      this.startIndex = -1;
      builderBuffer = new StringBuilder(128);
    }

    public void preRead(char[] cbuf) {
      this.cbuf = cbuf;
      if (startIndex != -1) {
        if (builderBuffer.length() == 0) {
          builderBuffer.append(cbuf, startIndex + previousOffset, previousLength - startIndex);
        } else {
          builderBuffer.append(cbuf, previousOffset, previousLength);
        }
      }
    }

    public void postRead(int result, int offset) {
      if (startIndex == -1) {
        currentStartOffset = currentEndOffset;
      }
      this.previousOffset = offset;
      this.previousLength = result;
      this.currentEndOffset += result;
    }

    public void setStartIndex(int absoluteStartIndex) {
      int startIndexRelativeToStartOffset = Math.max(absoluteStartIndex - currentStartOffset, 0);
      this.startIndex = findProperStartIndex(startIndexRelativeToStartOffset);

    }

    public String readRawText(int absoluteEndIndex) {
      int endIndexRelativeToStartOffset = absoluteEndIndex - currentStartOffset;
      if (builderBuffer.length() == 0) {
        // Text is in cbuf
        if (startIndex >= endIndexRelativeToStartOffset) {
          // Occurs with <test/> and <test></test>
          return "";
        }
        builderBuffer.append(cbuf, startIndex, endIndexRelativeToStartOffset - startIndex);
      } else {
        // Part of text is in builderBuffer
        int captureLength = endIndexRelativeToStartOffset - startIndex;
        currentStartOffset = currentEndOffset - previousLength - previousOffset;
        if (captureLength <= builderBuffer.length()) {
          // Text is in builderBuffer
          builderBuffer.setLength(captureLength);
        } else {
          // Text is in builderBuffer and cbuf - append missing text from cbuf
          builderBuffer.append(cbuf, previousOffset, captureLength - builderBuffer.length());
        }
      }
      String rawText = builderBuffer.toString();
      builderBuffer.setLength(0);
      startIndex = -1;
      return rawText;
    }

    private int findProperStartIndex(int startIndex) {
      while (startIndex < cbuf.length && cbuf[startIndex] != '>') {
        startIndex++;
      }
      return ++startIndex;
    }
  }
}
