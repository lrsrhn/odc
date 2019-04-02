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

import com.ximpleware.VTDNav;
import dk.ott.core.finder.ElementFinder;

public final class VtdIndexProcessor extends BaseElementProcessor<VTDNav, XMLElement> {

    public VtdIndexProcessor(ElementFinder nextElementFinder, ObjectStore objectStore) {
        super(nextElementFinder, objectStore);
    }

    public ObjectStore search(VTDNav vtdNav, XMLElement xmlElement) throws Exception {
        int previousElementDepth = -1;
        int currentDepth = 0;
        IndexProgressStack indexProgressStack = new IndexProgressStack(10);
        if (vtdNav.toElement(VTDNav.ROOT)) {
            for (int tokenIndex = vtdNav.getCurrentIndex(); tokenIndex < vtdNav.getTokenCount(); tokenIndex++) {
                switch (vtdNav.getTokenType(tokenIndex)) {
                    case VTDNav.TOKEN_STARTING_TAG:
                        int elementDepth = vtdNav.getTokenDepth(tokenIndex);
                        if (elementDepth > previousElementDepth) {
                            indexProgressStack.push(tokenIndex, vtdNav.toNormalizedString(tokenIndex));
                        } else {
                            while (elementDepth <= previousElementDepth) {
                                IndexProgressStack.Element element = indexProgressStack.pop();
                                xmlElement.setElementName(element.getElementName());
                                xmlElement.setTokenIndex(element.getElementIndex());
                                observableTreeTraverser.endElement(xmlElement, --currentDepth);
                                previousElementDepth--;
                            }
                            indexProgressStack.push(tokenIndex, vtdNav.toNormalizedString(tokenIndex));
                        }
                        previousElementDepth = elementDepth;
                        xmlElement.setTokenIndex(tokenIndex);
                        observableTreeTraverser.startElement(xmlElement, currentDepth++);
                        break;
                    case VTDNav.TOKEN_CHARACTER_DATA:
                        xmlElement.setTokenIndex(tokenIndex);
                        observableTreeTraverser.text(xmlElement);
                        break;
                }
            }
            // Stack not empty -> process end elements
            while (!indexProgressStack.isEmpty()) {
                IndexProgressStack.Element element = indexProgressStack.pop();
                xmlElement.setElementName(element.getElementName());
                xmlElement.setTokenIndex(element.getElementIndex());
                observableTreeTraverser.endElement(xmlElement, --currentDepth);
            }
        }
        return objectStore;
    }
}