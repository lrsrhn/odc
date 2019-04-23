package dk.ott.core.processing;

import java.util.Arrays;

public class IndexProgressStack {
    private Element[] elementIndices;
    private int nextChildIndex;

    IndexProgressStack(int size) {
        this.elementIndices = new Element[size];
        prefill(0);
        this.nextChildIndex = -1;
    }

    private void prefill(int startIndex) {
        for (int i = startIndex; i < elementIndices.length; i++) {
            elementIndices[i] = new Element();
        }
    }

    void push(int elementIndex, String elementName) {
        if (this.nextChildIndex == elementIndices.length - 1) {
            int previousSize = elementIndices.length;
            elementIndices = Arrays.copyOf(elementIndices, elementIndices.length * 2);
            prefill(previousSize);
        }
        elementIndices[++this.nextChildIndex].setValues(elementIndex, elementName);
    }

    Element pop() {
        if (isEmpty()) {
            return null;
        }
        return elementIndices[nextChildIndex--];
    }

    boolean isEmpty() {
        return nextChildIndex == -1;
    }

    public static class Element {
        private int elementIndex;
        private String elementName;

        Element() { }

        void setValues(int elementIndex, String elementName) {
            this.elementIndex = elementIndex;
            this.elementName = elementName;
        }

        int getElementIndex() {
            return elementIndex;
        }

        String getElementName() {
            return elementName;
        }
    }
}
