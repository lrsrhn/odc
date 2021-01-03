package dk.ott.bintree;

import dk.ott.core.BinEdge;
import dk.ott.core.BinEdgeBuilder;
import dk.ott.predicate.Predicate;
import dk.ott.processing.ElementCursor;

import static dk.ott.bintree.NodeBuilder.nodeBuilder;

public class BinTree {
    private int size = 0;
//    Index[] indices = new Index[50];
    int[] nodes = new int[50];
    String[] elementNames = new String[50];
    Predicate[] predicates = new Predicate[50];
    BinEdge[] edges = new BinEdge[50];
    private PositionalIndex node;

    public BinTree() {
        buildElementIndex(0, null);
    }

    public int addIndex(int newNode) {
        nodes[size] = newNode;
        int parentNodeIndex = NodeOperations.getParentIndex(newNode);
        int parentNode = nodes[parentNodeIndex];
        int childNodeIndex = NodeOperations.getChildIndex(parentNode);
        if (childNodeIndex == 0) {
            nodes[parentNodeIndex] = nodeBuilder(parentNode).childNodeIndex(size).build();
        }
        return size++;
    }

    public BinEdge addEdge(int index) {
        BinEdge binEdge = edges[index];
        if (binEdge == null) {
            binEdge = new BinEdge();
            edges[index] = binEdge;
        }
        return binEdge;
    }

    public BinEdgeBuilder buildElementIndex(int parentIndex, String elementName) {
        int newNode = nodeBuilder()
                .parentNodeIndex(parentIndex)
                .nameLength(elementName != null ? elementName.length() : 0)
                .build();
        int newNodeIndex = addIndex(newNode);
        elementNames[newNodeIndex] = elementName;
        return new BinEdgeBuilder(new PositionalIndex(newNode, newNodeIndex), this, addEdge(newNodeIndex));
    }

    public int getRoot() {
        return nodes[0];
    }

    public boolean lookupIndex(PositionalIndex positionalIndex, ElementCursor elementCursor, boolean includeAbsolutes) {
        int node = positionalIndex.getNode();
        int parentIndex = positionalIndex.getPosition();
        int elementNameLength = elementCursor.getElementName().length();
        for (int i = NodeOperations.getChildIndex(node); i < size; i++) {
            int currentIndex = nodes[i];
            int currentNameLength = NodeOperations.getNameLength(currentIndex);
            if (NodeOperations.getParentIndex(currentIndex) != parentIndex || currentNameLength > elementNameLength) {
                return false;
            }
            if (currentNameLength == elementNameLength && elementNames[i].equals(elementCursor.getElementName())) {
                positionalIndex.setNode(currentIndex);
                positionalIndex.setPosition(i);
                return true;
            }
        }
        return false;
    }

    // TODO: 1/2/21 Start from here!
    public long lookupIndex(long positionalIndex, ElementCursor elementCursor, boolean includeAbsolutes) {
        int node = NodeOperations.positionalNodeToNode(positionalIndex);
        int parentIndex = NodeOperations.positionalNodeToIndex(positionalIndex);
        int elementNameLength = elementCursor.getElementName().length();
        for (int i = NodeOperations.getChildIndex(node); i < size; i++) {
            int currentIndex = nodes[i];
            int currentNameLength = NodeOperations.getNameLength(currentIndex);
            if (NodeOperations.getParentIndex(currentIndex) != parentIndex || currentNameLength > elementNameLength) {
                return -1;
            }
            if (currentNameLength == elementNameLength && elementNames[i].equals(elementCursor.getElementName())) {
                return NodeOperations.toPositionalNode(currentIndex, i);
            }
        }
        return -1;
    }

//    private int lookupChildIndex(int parentIndex, String elementName) {
//        int elementNameLength = elementName.length();
//        Index currentIndex;
//        for (int i = indices[parentIndex].childIndex; i < size && (currentIndex = indices[i]).parentIndex == parentIndex && currentIndex.nameLength <= elementNameLength; i++) {
//            if (currentIndex.nameLength == elementNameLength && elementNames[i].equals(elementName)) {
//                return i;
//            }
//        }
//        return -1;
//    }

    public int getNodeByIndex(int nodeIndex) {
        return nodes[nodeIndex];
    }

    public BinEdge getEdge(int index) {
        return edges[index];
    }

    public BinEdge getEdge(long positionalNode) {
        return edges[NodeOperations.positionalNodeToIndex(positionalNode)];
    }

    public void setNode(PositionalIndex node) {
        nodes[node.getPosition()] = node.getNode();
    }
}
