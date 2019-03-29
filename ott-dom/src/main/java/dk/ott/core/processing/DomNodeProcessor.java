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
package dk.ott.core.processing;

import dk.ott.core.finder.ElementFinder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.stream.XMLStreamReader;

public final class DomNodeProcessor extends BaseElementProcessor<Document, DomElement> {

    public DomNodeProcessor(ElementFinder nextElementFinder, ObjectStore objectStore) {
        super(nextElementFinder, objectStore);
    }

    public ObjectStore search(Document document, DomElement domElement) throws Exception {
        int currentDepth = 0;
        NodeProgressStack nodeProgressStack = new NodeProgressStack(10);
        NodeProgressStack.NodeProgress currentNodeProgress = new NodeProgressStack.NodeProgress();
        currentNodeProgress.setValues(0, document.getDocumentElement());

        while (currentNodeProgress != null) {
            Node currentNode = currentNodeProgress.getNodeElement();
            int index = currentNodeProgress.getIndex();
            domElement.setNode(currentNode);
            if (index == 0) {
                observableTreeTraverser.startElement(domElement, currentDepth++);
            }
            if (currentNode.hasChildNodes()) {
                NodeList nodeList = currentNode.getChildNodes();
                for (; index < nodeList.getLength(); index++) {
                    Node childNode = nodeList.item(index);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        currentNodeProgress.setValues(index + 1, currentNode); // Save progress
                        currentNodeProgress = nodeProgressStack.push(0, childNode);
                        break;
                    } else if (childNode.getNodeType() == Node.TEXT_NODE) {
                        observableTreeTraverser.text(domElement);
                    }
                }
                if (index == nodeList.getLength()) {
                    observableTreeTraverser.endElement(domElement, --currentDepth);
                    currentNodeProgress = nodeProgressStack.pop();
                }
            } else {
                observableTreeTraverser.endElement(domElement, --currentDepth);
                currentNodeProgress = nodeProgressStack.pop();
            }
        }
        return objectStore;
    }
}
