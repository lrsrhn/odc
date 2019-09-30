package dk.ott.core;

import dk.ott.predicate.Predicate;

public abstract class NodeBase implements Node {
    NodeReference thisReference;
    Edge otherwise;

    public NodeBase(NodeReference thisReference, Edge otherwise) {
        this.thisReference = thisReference;
        this.otherwise = otherwise;
        thisReference.setNode(this);
    }

    public NodeBase() {
        this.thisReference = new NodeReference(this);
        this.otherwise = null;
    }

    @Override
    public EdgeBuilder buildEdge(String searchElement, boolean isRelative) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public EdgeBuilder buildEdge(Predicate predicate) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public NodeReference getReference() {
        return thisReference;
    }

    @Override
    public Node getDereference() {
        return this;
    }

    @Override
    public Edge getOtherwise() {
        return otherwise;
    }

    @Override
    public void setOtherwise(Edge otherwise) {
        this.otherwise = otherwise;
    }
}
