package dk.ott.dsl.parser;

public class Element {
    private String element;
    private boolean isRelative;

    public Element(String element, boolean isRelative) {
        this.element = element;
        this.isRelative = isRelative;
    }

    public String getElement() {
        return element;
    }

    public boolean isRelative() {
        return isRelative;
    }
}
