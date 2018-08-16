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
package dk.simpletools.odc.core.processing;

import dk.simpletools.odc.core.finder.ElementFinder;
import dk.simpletools.odc.core.finder.OnEndHandler;

import java.util.Arrays;

final class ElementFinderStack {
    private ElementFinderStack.StackElement[] elementFinderStack;
    private int lookupIndex;

    ElementFinderStack(int size) {
        this.elementFinderStack = new ElementFinderStack.StackElement[size];
        prefill(0);
        this.lookupIndex = -1;
    }

    private void prefill(int startIndex) {
        for (int i = startIndex; i < elementFinderStack.length; i++) {
            elementFinderStack[i] = new StackElement();
        }
    }

    void push(int searchDepth, ElementFinder previousElementFinder, OnEndHandler onEndHandler) {
        if (lookupIndex == elementFinderStack.length - 1) {
            int previousSize = elementFinderStack.length;
            elementFinderStack = Arrays.copyOf(elementFinderStack, elementFinderStack.length * 2);
            prefill(previousSize);
        }
         elementFinderStack[++lookupIndex].setValues(onEndHandler, searchDepth, previousElementFinder);
    }

    ElementFinderStack.StackElement lookup(int searchDepth) {
        if (lookupIndex != -1) {
            ElementFinderStack.StackElement stackElement = elementFinderStack[lookupIndex];
            if (stackElement.getSearchDepth() == searchDepth) {
                lookupIndex--;
                return stackElement;
            }
        }
        return null;
    }

    static final class StackElement {
        private int searchDepth;
        private OnEndHandler onEndHandler;
        private ElementFinder previousElementFinder;

        OnEndHandler getOnEndHandler() {
            return onEndHandler;
        }

        ElementFinder getPreviousElementFinder() {
            return previousElementFinder;
        }

        int getSearchDepth() {return searchDepth;}

        void setValues(OnEndHandler onEndHandler, int searchDepth, ElementFinder previousElementFinder) {
            this.onEndHandler = onEndHandler;
            this.previousElementFinder = previousElementFinder;
            this.searchDepth = searchDepth;
        }
    }
}
