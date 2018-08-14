package dk.simpletools.odc.core.processing.stub;

import java.util.ArrayList;

public final class InputReader {
    private Element[] elements;
    private int index;

    InputReader(ArrayList<Element> elements) {
        this.elements = elements.toArray(new Element[elements.size()]);
        reset();
    }

    boolean hasNext() {
        return index + 1 < elements.length;
    }

    public Element next() {
        return elements[++index];
    }

    public void reset() {
        index = -1;
    }
}
