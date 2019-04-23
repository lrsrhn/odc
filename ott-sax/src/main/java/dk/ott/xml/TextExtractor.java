package dk.ott.xml;

public class TextExtractor {
    private static final int CAPACITY = 512;
    private StringBuilder stringBuilder;

    public TextExtractor() {
        this.stringBuilder = new StringBuilder(CAPACITY);
    }

    public void append(char[] str, int offset, int len) {
        stringBuilder.append(str, offset, len);
    }

    public String extract() {
        if (stringBuilder.length() == 0) {
            return null;
        }
        return stringBuilder.toString();
    }

    public void clear() {
        if (stringBuilder.capacity() > CAPACITY) {
            stringBuilder = new StringBuilder(CAPACITY);
        } else {
            stringBuilder.setLength(0);
        }
    }
}
