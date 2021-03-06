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

import dk.ott.core.Edge;
import dk.ott.core.Node;
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
    private Node currentNode;
    private TextLocation currentOnTextLocation;
    private int parentDepth;
    private int childDepth;

    public ObservableTreeTraverser(final Node rootNode, ObjectStore objectStore) {
        this.objectStore = objectStore;
        this.currentNode = rootNode;
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
        Edge edge = currentNode.lookupEdge(elementCursor, objectStore, childDepth == currentDepth);
        if (edge != null) {
//            System.out.println("StartElement: handle absolute edge");
            return handleEdge(edge, elementCursor, currentDepth);
        }
        Edge otherwise = currentNode.getOtherwise();
        if (otherwise != null) {
            return handleEdge(otherwise, elementCursor, currentDepth);
        }
        return !currentNode.hasRelative() ? EventAction.SKIP_ELEMENT : EventAction.CONTINUE;
    }

    private EventAction handleEdge(final Edge edge, final InternalElementCursor elementCursor, final int currentDepth) throws Exception {
        OnStartHandler onStartHandler = edge.getOnStartHandler();
        Predicate filter = edge.getFilter();
        currentOnTextLocation = edge.getTextLocation();
        OnEndHandler onEndHandler = edge.getOnEndHandler();
        Node nextNode = edge.getChildNode();
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
            if (nextNode != null) {
                handleStacks(currentDepth, null);
                handleNextElementFinder(elementCursor, currentDepth, nextNode);
            } else if (currentOnTextLocation != null) {
                handleStacks(currentDepth, null);
                return currentOnTextLocation.isRaw() ? EventAction.READ_RAW_TEXT : EventAction.CONTINUE;
            } else {
                return EventAction.SKIP_ELEMENT;
            }
        } else {
            handleStacks(currentDepth, onEndHandler);
            if (nextNode != null) {
                handleNextElementFinder(elementCursor, currentDepth, nextNode);
            }
        }
        return EventAction.CONTINUE;
    }

    private void handleStacks(final int currentDepth, final OnEndHandler onEndHandler) {
        depthStack.push(currentDepth);
        parentDepth = currentDepth;
        childDepth = parentDepth + 1;
        nodeStack.push(currentNode, onEndHandler);
    }

    private void handleNextElementFinder(final InternalElementCursor elementCursor, final int currentDepth, final Node nextNode) throws Exception {
        currentNode = nextNode;
        if (currentNode.isPredicate()) {
            Edge edge = currentNode.lookupEdge(elementCursor, objectStore);
            if (edge != null) {
                handleEdge(edge, elementCursor, currentDepth);
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
//        System.out.println("EndElement: '" + elementCursor.getElementName() + "'");
//            System.out.println("EndElement by handler");
        if (parentDepth == currentDepth) {
            handleEndElement(elementCursor);
            if (currentNode.isPredicate()) {
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
        Node previousNode = stackItem.getPreviousNode();
        elementCursor.clearCache();
        if (onEndHandler != null) {
            onEndHandler.onEnd(elementCursor, objectStore);
        }
        currentNode = previousNode;
    }

    public boolean isTextHandlerSet() {
        return currentOnTextLocation != null;
    }
}
