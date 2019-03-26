package dk.simpletools.odc.xml;

public class TextExtractor {
    public static final int CAPACITY = 512;
    private StringBuilder stringBuilder;
    private boolean isBuffering;

    public TextExtractor() {
        this.stringBuilder = new StringBuilder(CAPACITY);
        this.isBuffering = false;
    }

    public void startBuffering() {
        isBuffering = true;
    }

    public void append(char[] str, int offset, int len) {
        if (isBuffering) {
            stringBuilder.append(str, offset, len);
        }
    }

    public String extract() {
        if (isBuffering) {
            return stringBuilder.toString();
        }
        return null;
    }

    public void clear() {
        isBuffering = false;
        if (stringBuilder.capacity() > CAPACITY) {
            stringBuilder = new StringBuilder();
        } else {
            stringBuilder.setLength(0);
        }
    }
}
