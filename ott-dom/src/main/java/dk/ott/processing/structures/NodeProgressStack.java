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

import org.w3c.dom.Node;

import java.util.Arrays;

public class NodeProgressStack {
    private NodeProgressItem[] nodeProgressItems;
    private int nextChildIndex;

    public NodeProgressStack(int size) {
        this.nodeProgressItems = new NodeProgressItem[size];
        prefill(0);
        this.nextChildIndex = -1;
    }

    private void prefill(int startIndex) {
        for (int i = startIndex; i < nodeProgressItems.length; i++) {
            nodeProgressItems[i] = new NodeProgressItem();
        }
    }

    public void push(int nextChildIndex, Node node) {
        if (this.nextChildIndex == nodeProgressItems.length - 1) {
            int previousSize = nodeProgressItems.length;
            nodeProgressItems = Arrays.copyOf(nodeProgressItems, nodeProgressItems.length * 2);
            prefill(previousSize);
        }
        nodeProgressItems[++this.nextChildIndex].setValues(nextChildIndex, node);
    }

    public NodeProgressItem pop() {
        if (isEmpty()) {
            return null;
        }
        return nodeProgressItems[nextChildIndex--];
    }

    public boolean isEmpty() {
        return nextChildIndex == -1;
    }
}
