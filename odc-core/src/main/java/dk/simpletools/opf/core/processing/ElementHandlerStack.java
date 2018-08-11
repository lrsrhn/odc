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
package dk.simpletools.opf.core.processing;

import dk.simpletools.opf.core.finder.ElementFinder;
import dk.simpletools.opf.core.finder.OnEndHandler;

import java.util.ArrayList;
import java.util.List;

public class ElementHandlerStack {
  private List<StackElement> elementHandlerList;
  private int lookupIndex;

  public ElementHandlerStack(int size) {
    this.elementHandlerList = new ArrayList<StackElement>(size);
    this.lookupIndex = -1;
  }

  public void push(ElementFinder elementFinder, OnEndHandler onEndHandler, int searchDepth) {
    if (onEndHandler == null) {
      return;
    }
    elementHandlerList.add(new StackElement(elementFinder, onEndHandler, searchDepth));
    lookupIndex++;
  }

  public OnEndHandler lookup(ElementFinder elementFinder, int searchDepth) {
    return peek(elementFinder, searchDepth) != null ? elementHandlerList.remove(lookupIndex--).getOnEndHandler() : null;
  }

  public OnEndHandler peek(ElementFinder elementFinder, int searchDepth) {
    if (elementHandlerList.isEmpty()) {
      return null;
    }
    StackElement stackElement = elementHandlerList.get(lookupIndex);
    if (stackElement.getElementFinder() != elementFinder) {
      return null;
    }
    if (stackElement.getSearchDepth() != searchDepth) {
      return null;
    }
    return stackElement.getOnEndHandler();
  }

  private static class StackElement {
    private ElementFinder elementFinder;
    private OnEndHandler onEndHandler;
    private int searchDepth;

    public StackElement(ElementFinder elementFinder, OnEndHandler onEndHandler, int searchDepth) {
      this.elementFinder = elementFinder;
      this.onEndHandler = onEndHandler;
      this.searchDepth = searchDepth;
    }

    public ElementFinder getElementFinder() {
      return elementFinder;
    }

    public OnEndHandler getOnEndHandler() {
      return onEndHandler;
    }

    public int getSearchDepth() {
      return searchDepth;
    }
  }
}
