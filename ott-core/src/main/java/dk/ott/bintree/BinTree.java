package dk.ott.bintree;

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
    Edge[] edges = new Edge[50];

    public BinTree() {
        buildElementIndex(-1, null, null);
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

    public EdgeBuilder buildElementIndex(int parentIndex, String elementName) {

        lookupChildIndex(parentIndex, elementName);
        Index index = new Index();
        index.parentIndex = (short) parentIndex;
        index.nameLength = elementName != null ? (short)  elementName.length() : 0;
        index.hasTextHandler = onTextHandler != null;

        int indexKey = addIndex(index);
        setElementName(indexKey, elementName);
        setOnTextHandler(indexKey, onTextHandler);
        return indexKey;
    }

    public Index getRoot() {
        return indices[0];
    }

    public void setElementName(int indexKey, String elementName) {
        if (elementName != null) {
            elementNames[indexKey] = elementName;
        }
    }

    public void setPredicate(int indexKey, Predicate predicate) {
        predicates[indexKey] = predicate;
    }

    public void setOnTextHandler(int indexKey, OnTextHandler onTextHandler) {
        if (onTextHandler != null) {
            onTextHandlers[indexKey] = onTextHandler;
        }
    }

    public void setOnStartHandler(int indexKey, OnStartHandler onStartHandler) {
        onStartHandlers[indexKey] = onStartHandler;
    }

    public void setOnEndHandlers(int indexKey, OnEndHandler onEndHandler) {
        onEndHandlers[indexKey] = onEndHandler;
    }

    public boolean lookupIndex(PositionalIndex positionalIndex, ElementCursor elementCursor, ObjectStore objectStore, boolean includeAbsolutes) {
        int firstChildIndex = positionalIndex.getIndex().childIndex;
        int parentIndex = positionalIndex.getPosition();
        int elementNameLength = elementCursor.getElementName().length();
        Index currentIndex;
        for (int i = firstChildIndex; i < size && (currentIndex = indices[i]).parentIndex == parentIndex && currentIndex.nameLength <= elementNameLength; i++) {
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

    public OnTextHandler getOnTextHandler(int currentIndexKey) {
        return onTextHandlers[currentIndexKey];
    }

    public Index getIndex(int indexKey) {
        return indices[indexKey];
    }
}
