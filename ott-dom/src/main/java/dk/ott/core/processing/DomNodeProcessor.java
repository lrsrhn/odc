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
package dk.ott.core.processing;

import dk.ott.core.finder.ElementFinder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DomNodeProcessor extends BaseElementProcessor<Document, DomElement> {
    private boolean skipElement;

    public DomNodeProcessor(ElementFinder nextElementFinder, ObjectStore objectStore) {
        super(nextElementFinder, objectStore);
        this.skipElement = false;
    }

    public ObjectStore search(Document document, DomElement domElement) throws Exception {
        int currentDepth = 0;
        NodeProgressStack nodeProgressStack = new NodeProgressStack(10);
        nodeProgressStack.push(0, document.getDocumentElement());

        for (NodeProgressStack.NodeProgress currentNodeProgress = nodeProgressStack.pop(); currentNodeProgress != null && !domElement.mustStopProcessing(); currentNodeProgress = nodeProgressStack.pop()) {
            Node currentNode = currentNodeProgress.getNodeElement();
            int nextChildIndex = currentNodeProgress.getNextChildIndex();
            domElement.setNode(currentNode);
            if (nextChildIndex == 0) {
                skipElement = observableTreeTraverser.startElement(domElement, currentDepth++) == EventAction.SKIP_ELEMENT;
            }
            if (!skipElement && currentNode.hasChildNodes()) {
                NodeList nodeList = currentNode.getChildNodes();
                for (; nextChildIndex < nodeList.getLength(); nextChildIndex++) {
                    Node childNode = nodeList.item(nextChildIndex);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        nodeProgressStack.push(nextChildIndex + 1, currentNode); // Save progress
                        nodeProgressStack.push(0, childNode);
                        break;
                    } else if (observableTreeTraverser.isTextHandlerSet() && childNode.getNodeType() == Node.TEXT_NODE) {
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
