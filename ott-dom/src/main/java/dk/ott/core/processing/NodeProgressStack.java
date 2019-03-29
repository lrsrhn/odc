package dk.ott.core.processing;

import org.w3c.dom.Node;

import java.util.Arrays;

public class NodeProgressStack {
    private NodeProgress[] nodeProgresses;
    private int lookupIndex;

    public NodeProgressStack(int size) {
        this.nodeProgresses = new NodeProgress[size];
        prefill(0);
        this.lookupIndex = -1;
    }

    private void prefill(int startIndex) {
        for (int i = startIndex; i < nodeProgresses.length; i++) {
            nodeProgresses[i] = new NodeProgress();
        }
    }

    NodeProgress push(int index, Node node) {
        if (lookupIndex == nodeProgresses.length - 1) {
            int previousSize = nodeProgresses.length;
            nodeProgresses = Arrays.copyOf(nodeProgresses, nodeProgresses.length * 2);
            prefill(previousSize);
        }
        NodeProgress nodeProgress = nodeProgresses[++lookupIndex];
        nodeProgress.setValues(index, node);
        return nodeProgress;
    }

    public NodeProgress pop() {
        if (isEmpty()) {
            return null;
        }
        return nodeProgresses[lookupIndex--];
    }

    public boolean isEmpty() {
        return lookupIndex == -1;
    }

    public static class NodeProgress {
        private int index;
        private Node nodeElement;

        NodeProgress() { }

        void setValues(int index, Node nodeElement) {
            this.index = index;
            this.nodeElement = nodeElement;
        }

        public int getIndex() {
            return index;
        }

        public Node getNodeElement() {
            return nodeElement;
        }
    }
}
