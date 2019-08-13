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

import java.io.IOException;
import java.io.Writer;

public class AppendableToWriterAdapter extends Writer {
    private StringBuilder stringBuilder;

    public AppendableToWriterAdapter(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        stringBuilder.append(cbuf, off, len);
    }

    @Override
    public void write(int c) throws IOException {
        stringBuilder.append((char) c);
    }

    @Override
    public void write(String str) throws IOException {
        stringBuilder.append(str);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        stringBuilder.append(cbuf);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        stringBuilder.append(str, off, len);
    }

    @Override
    public Writer append(char c) throws IOException {
        stringBuilder.append(c);
        return this;
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        stringBuilder.append(csq);
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        return super.append(csq, start, end);
    }

    @Override
    public void flush() throws IOException {
        // Ignore
    }

    @Override
    public void close() throws IOException {
        // Ignore
    }
}
