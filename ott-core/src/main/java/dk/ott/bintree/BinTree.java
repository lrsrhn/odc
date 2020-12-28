package dk.ott.bintree;

import dk.ott.core.BinEdge;
import dk.ott.core.BinEdgeBuilder;
import dk.ott.core.Edge;
import dk.ott.core.EdgeBuilder;
import dk.ott.event.OnEndHandler;
import dk.ott.event.OnStartHandler;
import dk.ott.event.OnTextHandler;
import dk.ott.predicate.Predicate;
import dk.ott.processing.ElementCursor;
import dk.ott.processing.ObjectStore;

public class BinTree {
    private int size = 0;
    Index[] indices = new Index[50];
    String[] elementNames = new String[50];
    Predicate[] predicates = new Predicate[50];
    BinEdge[] edges = new BinEdge[50];

    public BinTree() {
        buildElementIndex(-1, null);
    }

    public int addIndex(Index newIndex) {
        indices[size] = newIndex;
        if (newIndex.parentIndex != -1) {
            Index parentIndex = indices[newIndex.parentIndex];
            if (parentIndex.childIndex == 0) {
                parentIndex.childIndex = (short) size;
            }
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
        Index index = new Index();
        index.parentIndex = (short) parentIndex;
        index.nameLength = elementName != null ? (short)  elementName.length() : 0;
        int indexIndex = addIndex(index);
        elementNames[indexIndex] = elementName;
        return new BinEdgeBuilder(index, addEdge(indexIndex));
    }

    public Index getRoot() {
        return indices[0];
    }

    public boolean lookupIndex(PositionalIndex positionalIndex, ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
        int firstChildIndex = positionalIndex.getIndex().childIndex;
        int parentIndex = positionalIndex.getPosition();
        int elementNameLength = elementCursor.getElementName().length();
        for (int i = firstChildIndex; i < size; i++) {
            Index currentIndex = indices[i];
            if (currentIndex.parentIndex != parentIndex || currentIndex.nameLength > elementNameLength) {
                return false;
            }
            if (currentIndex.nameLength == elementNameLength && elementNames[i].equals(elementCursor.getElementName())) {
                positionalIndex.setPosition(i);
                positionalIndex.setIndex(currentIndex);
                return true;
            }
        }
        return false;
    }

    private int lookupChildIndex(int parentIndex, String elementName) {
        int elementNameLength = elementName.length();
        Index currentIndex;
        for (int i = indices[parentIndex].childIndex; i < size && (currentIndex = indices[i]).parentIndex == parentIndex && currentIndex.nameLength <= elementNameLength; i++) {
            if (currentIndex.nameLength == elementNameLength && elementNames[i].equals(elementName)) {
                return i;
            }
        }
        return -1;
    }

    public Index getIndex(int indexKey) {
        return indices[indexKey];
    }

    public BinEdge getEdge(int index) {
        return edges[index];
    }
}
