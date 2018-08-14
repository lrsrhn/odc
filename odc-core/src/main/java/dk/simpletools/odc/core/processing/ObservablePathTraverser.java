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

public final class ObservablePathTraverser {
    private ElementFinder currentElementFinder;
    private final EndElement endElement;
    private final ElementFinderStack elementFinderStack;
    private final IntStack depthStack;
    private int parentDepth;
    private int childDepth;

    ObservablePathTraverser(ElementFinder rootElementFinder, StructureElement structureElement) {
        this.endElement = new EndElement();
        this.endElement.setStructureElement(structureElement);
        this.currentElementFinder = rootElementFinder;
        this.elementFinderStack = new ElementFinderStack(15);
        this.depthStack = new IntStack(15);
        this.depthStack.push(-1);
        this.parentDepth = -1;
        this.childDepth = 0;
    }

    public void startElement(StructureElement structureElement, final int currentDepth) throws Exception {
        if (childDepth == currentDepth) {
            SearchLocation searchLocation = currentElementFinder.lookupSearchLocation(structureElement, false);
            if (searchLocation != null) {
                handleSearchLocation(searchLocation, structureElement, currentDepth);
                return;
            }
        }
        SearchLocation searchLocation = currentElementFinder.lookupSearchLocation(structureElement, true);
        if (searchLocation != null) {
            handleSearchLocation( searchLocation, structureElement, currentDepth);
        }
    }

    private void handleSearchLocation(SearchLocation searchLocation, StructureElement structureElement, final int currentDepth) throws Exception {
        OnStartHandler onStartHandler = searchLocation.getOnStartHandler();
        Predicate filter = searchLocation.getFilter();
        OnEndHandler onEndHandler = searchLocation.getOnEndHandler();
        ElementFinder nextElementFinder = searchLocation.getElementFinder();
        if (onStartHandler != null) {
            if (filter == null) {
                onStartHandler.startElement(structureElement);
            } else {
                if (filter.evaluate(structureElement)) {
                    onStartHandler.startElement(structureElement);
                }
            }
        }
        if (onEndHandler == null) {
            structureElement.clearCache();
            if (nextElementFinder != null) {
                handleStacks(currentDepth, null);
                handleNextElementFinder(structureElement, currentDepth, nextElementFinder);
            }
        } else {
            handleStacks(currentDepth, onEndHandler);
            if (nextElementFinder != null) {
                handleNextElementFinder(structureElement, currentDepth, nextElementFinder);
            }
        }
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
            OnEndHandler onEndHandler = stackElement.getOnEndHandler();
            ElementFinder previousElementFinder = stackElement.getPreviousElementFinder();
            if (onEndHandler != null) {
                structureElement.clearCache();
                onEndHandler.endElement(endElement, structureElement.getValueStore(), structureElement.getObjectStore());
            }
            currentElementFinder = previousElementFinder;
            endElement(structureElement, currentDepth);
        }
    }
}
