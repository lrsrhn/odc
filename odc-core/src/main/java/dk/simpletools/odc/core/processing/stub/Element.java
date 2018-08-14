package dk.simpletools.odc.core.processing.stub;

public class Element {
    private String elementName;
    private String textValue;
    private boolean isStartElement;

    public Element(String elementName, String textValue, boolean isStartElement) {
        this.elementName = elementName;
        this.textValue = textValue;
        this.isStartElement = isStartElement;
    }

    public String getElementName() {
        return elementName;
    }

    public String getTextValue() {
        return textValue;
    }

    public boolean isStartElement() {
        return isStartElement;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }
}
