package dk.ott.processing;

public class TextTrimmer {

    public static String trimToNull(String text) {
    if (text == null) {
      return null;
    }
    text = text.trim();
    if (text.isEmpty()) {
      return null;
    }
    return text;
  }
}
