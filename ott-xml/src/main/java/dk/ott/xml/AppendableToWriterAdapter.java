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
