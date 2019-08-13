package dk.ott.processing.structures;

import dk.ott.event.OnEndHandler;
import dk.ott.core.Node;

public class StackItem {
    private OnEndHandler onEndHandler;
    private Node previousNode;

    public OnEndHandler getOnEndHandler() {
        return onEndHandler;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setValues(OnEndHandler onEndHandler, Node previousNode) {
        this.onEndHandler = onEndHandler;
        this.previousNode = previousNode;
    }
}
