package dk.ott.bintree;

public class PositionalIndex {
    private int node;
    private int position;

    public PositionalIndex(int node, int position) {
        this.node = node;
        this.position = position;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void copyFrom(PositionalIndex from) {
        this.node = from.node;
        this.position = from.position;
    }
}
