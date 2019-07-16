package dk.ott.xml;

import java.io.IOException;
import java.io.OutputStream;

public class SwappableByteOutputStream extends OutputStream {

    private OutputStream outputStream;

    public SwappableByteOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream swapOutputStream(OutputStream newOutputStream) {
        OutputStream oldOutputStream = outputStream;
        outputStream = newOutputStream;
        return oldOutputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
