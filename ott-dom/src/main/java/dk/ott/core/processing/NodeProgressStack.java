package dk.ott.core.processing;

import org.w3c.dom.Node;

import java.util.Arrays;

public class NodeProgressStack {
    private NodeProgress[] nodeProgresses;
    private int nextChildIndex;

    public NodeProgressStack(int size) {
        this.nodeProgresses = new NodeProgress[size];
        prefill(0);
        this.nextChildIndex = -1;
    }

    private void prefill(int startIndex) {
        for (int i = startIndex; i < nodeProgresses.length; i++) {
            nodeProgresses[i] = new NodeProgress();
        }
    }

    void push(int nextChildIndex, Node node) {
        if (this.nextChildIndex == nodeProgresses.length - 1) {
            int previousSize = nodeProgresses.length;
            nodeProgresses = Arrays.copyOf(nodeProgresses, nodeProgresses.length * 2);
            prefill(previousSize);
        }
        nodeProgresses[++this.nextChildIndex].setValues(nextChildIndex, node);
    }

    public NodeProgress pop() {
        if (isEmpty()) {
            return null;
        }
        return nodeProgresses[nextChildIndex--];
    }

    public boolean isEmpty() {
        return nextChildIndex == -1;
    }

    public static class NodeProgress {
        private int nextChildIndex;
        private Node nodeElement;

        NodeProgress() { }

        void setValues(int nextChildIndex, Node nodeElement) {
            this.nextChildIndex = nextChildIndex;
            this.nodeElement = nodeElement;
        }

        public int getNextChildIndex() {
            return nextChildIndex;
        }

        public Node getNodeElement() {
            return nodeElement;
        }
    }
}
