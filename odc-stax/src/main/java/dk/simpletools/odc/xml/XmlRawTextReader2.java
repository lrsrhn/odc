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

import java.io.IOException;
import java.io.Reader;

public class XmlRawTextReader2 extends Reader {
    private Reader reader;
    private Buffer buffer;

    public XmlRawTextReader2(Reader reader) {
        this.reader = reader;
        this.buffer = new Buffer();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int result = reader.read(cbuf, off, len);
        buffer.addToBuffer(cbuf, off, len, result);
        return result;
    }

    @Override
    public int read() throws IOException {
        int result = super.read();
        buffer.addToBuffer(new char[] { (char) result }, 0, 1, result);
        return result;
    }

    @Override
    public int read(char[] cbuf) throws IOException {
        int result = super.read(cbuf);
        buffer.addToBuffer(cbuf, 0, cbuf.length, result);
        return result;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        buffer = null;
    }

    public void setStartIndex(int startIndex) { buffer.setStartIndex(startIndex);}

    public String readRawText(int endIndex) {
        return buffer.readRawText(endIndex);
    }

    private static class Buffer {
        private static final int BUILDER_BUFFER_CAPACITY = 8192;
        private StringBuilder builderBuffer;
        private int currentEndOffset;
        private int currentStartOffset;
        private int startIndex;

        Buffer() {
            this.currentEndOffset = 0;
            this.currentStartOffset = 0;
            this.startIndex = -1;
            builderBuffer = new StringBuilder(BUILDER_BUFFER_CAPACITY);
        }

        void setStartIndex(int absoluteStartIndex) {
            int startIndexRelativeToStartOffset = Math.max(absoluteStartIndex - currentStartOffset, 0);
            this.startIndex = findProperStartIndex(startIndexRelativeToStartOffset);

        }

        void addToBuffer(char[] cbuf, int off, int len, int result) {
            if (result > 0) {
                if (startIndex != -1) {
                    appendDataToBuffer(cbuf, off, len, result);
                } else {
                    insertDataInBuffer(cbuf, off, len, result);
                }
            }
        }

        private void appendDataToBuffer(char[] cbuf, int off, int len, int result) {
            currentEndOffset += result; // Move endOffset the amount of read characters
            // Append from cbuf by offset and length
            builderBuffer.append(cbuf, off, result);
        }

        private void insertDataInBuffer(char[] cbuf, int off, int len, int result) {
            // Move startOffset to endOffset and take current offset into account
            currentStartOffset = currentEndOffset;
            currentEndOffset += result; // Move endOffset the amount of read characters
            // Make complete cbuf copy
            builderBuffer.setLength(0);
            builderBuffer.append(cbuf, off, result);
        }

        String readRawText(int endIndex) {
            endIndex = endIndex - currentStartOffset;
            if (startIndex >= endIndex) {
                // Occurs with <test/> and <test></test>
                return "";
            }
            String text = builderBuffer.substring(startIndex, endIndex);
            startIndex = -1;
            return text;
        }

        private int findProperStartIndex(int startIndex) {
            while (startIndex < builderBuffer.length() && builderBuffer.charAt(startIndex) != '>') {
                startIndex++;
            }
            return startIndex + 1; // We don't want the actual '>'
        }
    }
}
