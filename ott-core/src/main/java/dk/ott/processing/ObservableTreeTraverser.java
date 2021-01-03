/**
 * The MIT License
 * Copyright © 2018 Lars Storm
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dk.ott.processing;

import dk.ott.bintree.BinTree;
import dk.ott.bintree.NodeOperations;
import dk.ott.bintree.PositionalIndex;
import dk.ott.core.BinEdge;
import dk.ott.core.TextLocation;
import dk.ott.event.OnEndHandler;
import dk.ott.event.OnStartHandler;
import dk.ott.predicate.Predicate;
import dk.ott.processing.structures.IntStack;
import dk.ott.processing.structures.NodeStack;
import dk.ott.processing.structures.StackItem;

public final class ObservableTreeTraverser {
    private final NodeStack nodeStack;
    private final IntStack depthStack;
    private final ObjectStore objectStore;
    private PositionalIndex currentIndex;
    private PositionalIndex childIndex;
    private BinTree binTree;
    private TextLocation currentOnTextLocation;
    private int parentDepth;
    private int childDepth;

    public ObservableTreeTraverser(final BinTree binTree, ObjectStore objectStore) {
        this.objectStore = objectStore;
        this.binTree = binTree;
        this.currentIndex = new PositionalIndex(binTree.getRoot(), 0);
        this.childIndex = new PositionalIndex(binTree.getRoot(), 0);
        this.nodeStack = new NodeStack(15);
        this.depthStack = new IntStack(15);
        this.depthStack.push(-1);
        this.parentDepth = -1;
        this.childDepth = 0;
    }

    public EventAction startElement(final InternalElementCursor elementCursor, final int currentDepth) throws Exception {
        elementCursor.clearCache();
        currentOnTextLocation = null;
//        System.out.println("StartElement: '" + elementCursor.getElementName() + "'");
        if (binTree.lookupIndex(childIndex, elementCursor, childDepth == currentDepth)) {
//            System.out.println("StartElement: handle absolute edge");
            return handleEdge(elementCursor, childIndex, currentDepth);
        }
        // TODO: 12/27/20 support this transparently in BinTree by sorting
//        Edge otherwise = currentNode.getOtherwise();
//        if (otherwise != null) {
//            return handleEdge(otherwise, elementCursor, currentDepth);
//        }
        return NodeOperations.hasRelativeChildren(currentIndex.getNode()) ? EventAction.CONTINUE : EventAction.SKIP_ELEMENT;
    }

    private EventAction handleEdge(final InternalElementCursor elementCursor, PositionalIndex childIndex, final int currentDepth) throws Exception {
        BinEdge edge = binTree.getEdge(childIndex.getPosition());
        OnStartHandler onStartHandler = edge.getOnStartHandler();
        Predicate filter = edge.getFilter();
        currentOnTextLocation = edge.getTextLocation();
        OnEndHandler onEndHandler = edge.getOnEndHandler();
        if (onStartHandler != null) {
            if (filter == null) {
                onStartHandler.onStart(elementCursor, objectStore);
            } else {
                if (filter.evaluate(elementCursor, objectStore)) {
                    onStartHandler.onStart(elementCursor, objectStore);
                }
            }
        }
        if (onEndHandler == null) {
            if (NodeOperations.getChildIndex(childIndex.getNode()) != 0) {
                handleStacks(currentDepth, null);
                handleNextElementFinder(elementCursor, childIndex, currentDepth);
            } else if (currentOnTextLocation != null) {
                handleStacks(currentDepth, null);
                return currentOnTextLocation.isRaw() ? EventAction.READ_RAW_TEXT : EventAction.CONTINUE;
            } else {
                return EventAction.SKIP_ELEMENT;
            }
        } else {
            handleStacks(currentDepth, onEndHandler);
            if (NodeOperations.getChildIndex(childIndex.getNode()) != 0) {
                handleNextElementFinder(elementCursor, childIndex, currentDepth);
            }
        }
        return EventAction.CONTINUE;
    }

    private void handleStacks(final int currentDepth, final OnEndHandler onEndHandler) {
        depthStack.push(currentDepth);
        parentDepth = currentDepth;
        childDepth = parentDepth + 1;
//        nodeStack.push(currentIndex.getNode(), currentIndex.getPosition(), onEndHandler);
    }

    private void handleNextElementFinder(final InternalElementCursor elementCursor, PositionalIndex childIndex, final int currentDepth) throws Exception {
        currentIndex.copyFrom(childIndex);
        if (NodeOperations.hasPredicateChildren(currentIndex.getNode())) {
            if (binTree.lookupIndex(childIndex, elementCursor, childDepth == currentDepth)) {
                handleEdge(elementCursor, childIndex, currentDepth);
            }
        }
    }

    public void text(final InternalElementCursor elementCursor) throws Exception {
//        System.out.println("Text event");
        if (currentOnTextLocation != null && elementCursor.getText() != null) {
//            System.out.println("Text event by handler");
            Predicate filter = currentOnTextLocation.getTextFilter();
            if (filter == null) {
                currentOnTextLocation.getOnTextHandler().onText(elementCursor, objectStore);
            } else if (filter.evaluate(elementCursor, objectStore)) {
                currentOnTextLocation.getOnTextHandler().onText(elementCursor, objectStore);
            }
        }
    }

    public void endElement(final InternalElementCursor elementCursor, final int currentDepth) throws Exception {

//            System.out.println("EndElement by handler");
        if (parentDepth == currentDepth) {
//            System.out.println("EndElement: '" + elementCursor.getElementName() + "'");
            handleEndElement(elementCursor);
            if (NodeOperations.isPredicate(currentIndex.getNode())) {
                handleEndElement(elementCursor);
            }
        }
    }

    private void handleEndElement(InternalElementCursor elementCursor) throws Exception {
        childDepth = parentDepth;
        parentDepth = depthStack.popAndPeek();
        currentOnTextLocation = null;
        StackItem stackItem = nodeStack.pop();
        OnEndHandler onEndHandler = stackItem.getOnEndHandler();
        elementCursor.clearCache();
        if (onEndHandler != null) {
            onEndHandler.onEnd(elementCursor, objectStore);
        }
//        currentIndex.setPosition(stackItem.getPreviousNodeIndex());
//        currentIndex.setNode(stackItem.getPreviousPositionalNode());
//        childIndex.setPosition(stackItem.getPreviousNodeIndex());
//        childIndex.setNode(stackItem.getPreviousPositionalNode());
    }

    public boolean isTextHandlerSet() {
        return currentOnTextLocation != null;
    }
}
