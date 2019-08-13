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
package dk.ott.processing.structures;

import dk.ott.core.Node;
import dk.ott.event.OnEndHandler;

import java.util.Arrays;

public final class NodeStack {
    private StackItem[] stackItems;
    private int lookupIndex;

    public NodeStack(int size) {
        this.stackItems = new StackItem[size];
        prefill(0);
        this.lookupIndex = -1;
    }

    private void prefill(int startIndex) {
        for (int i = startIndex; i < stackItems.length; i++) {
            stackItems[i] = new StackItem();
        }
    }

    public void push(Node previousNode, OnEndHandler onEndHandler) {
        if (lookupIndex == stackItems.length - 1) {
            int previousSize = stackItems.length;
            stackItems = Arrays.copyOf(stackItems, stackItems.length * 2);
            prefill(previousSize);
        }
         stackItems[++lookupIndex].setValues(onEndHandler, previousNode);
    }

    public StackItem pop() {
        if (lookupIndex == -1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return stackItems[lookupIndex--];
    }

}
