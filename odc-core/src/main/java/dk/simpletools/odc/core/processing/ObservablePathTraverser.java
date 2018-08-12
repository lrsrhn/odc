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
import dk.simpletools.odc.core.finder.OnStartHandler;
import dk.simpletools.odc.core.finder.SearchLocation;
import dk.simpletools.odc.core.predicate.Predicate;

public class ObservablePathTraverser {
    private ElementFinder currentElementFinder;
    private final EndElement endElement;
    private final ElementFinderStack elementFinderStack;
    private final IntStack depthStack;
    private int parentDepth;
    private int childDepth;

    public ObservablePathTraverser(ElementFinder rootElementFinder, StructureElement structureElement) {
        this.endElement = new EndElement();
        this.endElement.setStructureElement(structureElement);
        this.currentElementFinder = rootElementFinder;
        this.elementFinderStack = new ElementFinderStack(30);
        this.depthStack = new IntStack(30);
        this.depthStack.push(-1);
        this.parentDepth = -1;
        this.childDepth = 0;
    }

    public void startElement(StructureElement structureElement, final int currentDepth) throws Exception {
        if (childDepth == currentDepth) {
            SearchLocation searchLocation = currentElementFinder.lookupSearchLocation(structureElement, false);
            if (searchLocation != null && handleSearchLocation(searchLocation, structureElement, currentDepth)) {
                return;
            }
        }
        SearchLocation searchLocation = currentElementFinder.lookupSearchLocation(structureElement, true);
        if (searchLocation != null) {
            handleSearchLocation(searchLocation, structureElement, currentDepth);
        }
    }

    private boolean handleSearchLocation(SearchLocation searchLocation, StructureElement structureElement, final int currentDepth) throws Exception {
        OnStartHandler onStartHandler = searchLocation.getOnStartHandler();
        boolean handled = false;
        if (onStartHandler != null) {
            Predicate filter = searchLocation.getFilter();
            if (filter != null) {
                if (filter.evaluate(structureElement)) {
                    onStartHandler.startElement(structureElement);
                    handled = true;
                }
            } else {
                onStartHandler.startElement(structureElement);
                handled = true;
            }
        }
        OnEndHandler onEndHandler = searchLocation.getOnEndHandler();
        ElementFinder nextElementFinder = searchLocation.getElementFinder();
        if (onEndHandler != null) {
            handleStacks(currentDepth, onEndHandler);
            if (nextElementFinder != null) {
                handleNextElementFinder(structureElement, currentDepth, nextElementFinder);
                handled = true;
            }
        } else {
            structureElement.clearCache();
            if (nextElementFinder != null) {
                handleStacks(currentDepth, null);
                handleNextElementFinder(structureElement, currentDepth, nextElementFinder);
                handled = true;
            }
        }
        return handled;
    }

    private void handleStacks(int currentDepth, OnEndHandler onEndHandler) {
        depthStack.push(currentDepth);
        parentDepth = currentDepth;
        childDepth = parentDepth + 1;
        elementFinderStack.push(currentDepth, currentElementFinder, onEndHandler);
    }

    private void handleNextElementFinder(StructureElement structureElement, final int currentDepth, ElementFinder nextElementFinder) throws Exception {
        currentElementFinder = nextElementFinder;
        if (currentElementFinder.isPredicate()) {
            SearchLocation searchLocation = currentElementFinder.lookupSearchLocation(structureElement, false);
            if (searchLocation != null) {
                handleSearchLocation(searchLocation, structureElement, currentDepth);
            }
        }
    }

    public void endElement(StructureElement structureElement, final int currentDepth) throws Exception {
        if (parentDepth == currentDepth) {
            depthStack.pop();
            childDepth = parentDepth;
            parentDepth = depthStack.peek();
            ElementFinderStack.StackElement stackElement = elementFinderStack.lookup(currentDepth);
            if (stackElement != null) {
                OnEndHandler onEndHandler = stackElement.getOnEndHandler();
                if (onEndHandler != null) {
                    structureElement.clearCache();
                    onEndHandler.endElement(endElement, structureElement.getValueStore(), structureElement.getObjectStore());
                }
                ElementFinder previousElementFinder = stackElement.getPreviousElementFinder();
                if (previousElementFinder != null) {
                    currentElementFinder = previousElementFinder;
                    endElement(structureElement, currentDepth);
                }
            }
        }
    }
}
