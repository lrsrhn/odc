/**
 * The MIT License
 * Copyright © 2018 Lars Storm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dk.ott.processing;

import dk.ott.core.Node;
import dk.ott.processing.structures.NodeProgressItem;
import dk.ott.processing.structures.NodeProgressStack;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public final class DomNodeProcessor extends BaseElementProcessor<Document, DomElementCursor> {
    private boolean skipElement;

    public DomNodeProcessor(Node nextNode, ObjectStore objectStore) {
        super(nextNode, objectStore);
        this.skipElement = false;
    }

    public ObjectStore search(Document document, DomElementCursor domElement) throws Exception {
        int currentDepth = 0;
        NodeProgressStack nodeProgressStack = new NodeProgressStack(10);
        nodeProgressStack.push(0, document.getDocumentElement());

        for (NodeProgressItem currentNodeProgressItem = nodeProgressStack.pop(); currentNodeProgressItem != null && !domElement.mustStopProcessing(); currentNodeProgressItem = nodeProgressStack.pop()) {
            org.w3c.dom.Node currentNode = currentNodeProgressItem.getNodeElement();
            int nextChildIndex = currentNodeProgressItem.getNextChildIndex();
            domElement.setNode(currentNode);
            if (nextChildIndex == 0) {
                skipElement = observableTreeTraverser.startElement(domElement, currentDepth++) == EventAction.SKIP_ELEMENT;
            }
            if (!skipElement && currentNode.hasChildNodes()) {
                NodeList nodeList = currentNode.getChildNodes();
                for (; nextChildIndex < nodeList.getLength(); nextChildIndex++) {
                    org.w3c.dom.Node childNode = nodeList.item(nextChildIndex);
                    if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        nodeProgressStack.push(nextChildIndex + 1, currentNode); // Save progress
                        nodeProgressStack.push(0, childNode);
                        break;
                    } else if (observableTreeTraverser.isTextHandlerSet() && childNode.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                        domElement.setNodeText(childNode);
                        observableTreeTraverser.text(domElement);
                    }
                }
                if (nextChildIndex == nodeList.getLength()) {
                    observableTreeTraverser.endElement(domElement, --currentDepth);
                }
            } else {
                observableTreeTraverser.endElement(domElement, --currentDepth);
                skipElement = false;
            }
        }
        return objectStore;
    }
}
