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

import dk.ott.bintree.Index;
import dk.ott.event.OnEndHandler;
import dk.ott.core.Node;

public class StackItem {
    private OnEndHandler onEndHandler;
    private Index previousNode;
    private int previousNodeIndex;

    public OnEndHandler getOnEndHandler() {
        return onEndHandler;
    }

    public Index getPreviousNode() {
        return previousNode;
    }

    public int getPreviousNodeIndex() {
        return previousNodeIndex;
    }

    public void setValues(OnEndHandler onEndHandler, Index previousNode, int previousNodeIndex) {
        this.onEndHandler = onEndHandler;
        this.previousNode = previousNode;
        this.previousNodeIndex = previousNodeIndex;
    }
}
