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
import dk.ott.core.stub.Element;
import dk.ott.core.stub.InputReader;

public class StubElementProcessor extends BaseElementProcessor<InputReader, ElementCursorContext> {

    public StubElementProcessor(ElementFinder rootElementFinder, ObjectStore objectStore) {
        super(rootElementFinder, objectStore);
    }

    @Override
    public ObjectStore search(InputReader parser, ElementCursorContext structureElement) throws Exception {
        int currentDepth = 0;
        while(parser.hasNext() && !structureElement.mustStopProcessing()) {
            Element element = parser.next();
            structureElement.setCurrentElement(element);
            if (element.isStartElement()) {
                super.observableTreeTraverser.startElement(structureElement, currentDepth++);
                super.observableTreeTraverser.text(structureElement);
                continue;
            }
            super.observableTreeTraverser.endElement(structureElement, --currentDepth);
        }
        return objectStore;
    }
}
