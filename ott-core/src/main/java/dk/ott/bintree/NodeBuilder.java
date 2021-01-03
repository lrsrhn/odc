package dk.ott.bintree;

public class NodeBuilder {
    private int node;

    private NodeBuilder(int node) {
        this.node = node;
    }

    public static NodeBuilder nodeBuilder() {
        return new NodeBuilder(0);
    }

    public static NodeBuilder nodeBuilder(int node) {
        return new NodeBuilder(node);
    }

    public NodeBuilder parentNodeIndex(int parentNodeIndex) {
        node = (node & 0x003FFFFF) | parentNodeIndex << 22 ;
        return this;
    }

    public NodeBuilder nameLength(int nameLength) {
        node = (node & 0xFFC03FFF) | nameLength << 14 ;
        return this;
    }

    public NodeBuilder hasTextHandler(boolean hasTextHandler) {
        int value = hasTextHandler ? 1 : 0;
        node = (node & 0xFFFFDFFF) | value << 13 ;
        return this;
    }

    public NodeBuilder isPredicate(boolean isPredicate) {
        int value = isPredicate ? 1 : 0;
        node = (node & 0xFFFFEFFF) | value << 12;
        return this;
    }

    public NodeBuilder isRelative(boolean isRelative) {
        int value = isRelative ? 1 : 0;
        node = (node & 0xFFFFF7FF) | value << 11;
        return this;
    }

    public NodeBuilder hasRelativeChildren(boolean hasRelativeChildren) {
        int value = hasRelativeChildren ? 1 : 0;
        node = (node & 0xFFFFFBFF) | value << 10;
        return this;
    }

    public NodeBuilder hasPredicateChildren(boolean hasPredicateChildren) {
        int value = hasPredicateChildren ? 1 : 0;
        node = (node & 0xFFFFFDFF) | value << 9;
        return this;
    }

    public NodeBuilder childNodeIndex(int childNodeIndex) {
        node = (node & 0xFFFFFE00) | childNodeIndex;
        return this;
    }

    public int build() {
        return node;
    }
}
