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

import dk.simpletools.odc.core.finder.*;
import dk.simpletools.odc.core.predicate.Predicate;

public final class ObservablePathTraverser {
    private ElementFinder currentElementFinder;
    private TextLocation currentOnTextLocation;
    private final ElementFinderStack elementFinderStack;
    private final IntStack depthStack;
    private int parentDepth;
    private int childDepth;
    private final ObjectStore objectStore;

    public ObservablePathTraverser(final ElementFinder rootElementFinder, final StructureElement structureElement, ObjectStore objectStore) {
        this.objectStore = objectStore;
        this.currentElementFinder = rootElementFinder;
        this.elementFinderStack = new ElementFinderStack(15);
        this.depthStack = new IntStack(15);
        this.depthStack.push(-1);
        this.parentDepth = -1;
        this.childDepth = 0;
    }

    public void startElement(final InternalStructureElement structureElement, final int currentDepth) throws Exception {
        structureElement.clearCache();
        structureElement.getElementName();
        structureElement.getElementNS();
        if (childDepth == currentDepth) {
            SearchLocation searchLocation = currentElementFinder.lookupSearchLocation(structureElement, objectStore, false);
            if (searchLocation != null) {
                handleSearchLocation(searchLocation, structureElement, currentDepth);
                return;
            } else if (!currentElementFinder.hasRelative()) {
                structureElement.skipElement();
                return;
            }
        }
        SearchLocation searchLocation = currentElementFinder.lookupSearchLocation(structureElement, objectStore, true);
        if (searchLocation != null) {
            handleSearchLocation( searchLocation, structureElement, currentDepth);
        }
    }

    private void handleSearchLocation(final SearchLocation searchLocation, final InternalStructureElement structureElement, final int currentDepth) throws Exception {
        OnStartHandler onStartHandler = searchLocation.getOnStartHandler();
        Predicate filter = searchLocation.getFilter();
        currentOnTextLocation = searchLocation.getTextLocation();
        OnEndHandler onEndHandler = searchLocation.getOnEndHandler();
        ElementFinder nextElementFinder = searchLocation.getElementFinder();
        if (onStartHandler != null) {
            if (filter == null) {
                onStartHandler.startElement(structureElement, objectStore);
            } else {
                if (filter.evaluate(structureElement, objectStore)) {
                    onStartHandler.startElement(structureElement, objectStore);
                }
            }
        }
        if (onEndHandler == null) {

            if (nextElementFinder != null) {
                handleStacks(currentDepth, null);
                handleNextElementFinder(structureElement, currentDepth, nextElementFinder);
            }
            else if (currentOnTextLocation != null) {
                handleStacks(currentDepth, null);
            }
        } else {
            handleStacks(currentDepth, onEndHandler);
            if (nextElementFinder != null) {
                handleNextElementFinder(structureElement, currentDepth, nextElementFinder);
            }
        }
    }

    private void handleStacks(final int currentDepth, final OnEndHandler onEndHandler) {
        depthStack.push(currentDepth);
        parentDepth = currentDepth;
        childDepth = parentDepth + 1;
        elementFinderStack.push(currentElementFinder, onEndHandler);
    }

    private void handleNextElementFinder(final InternalStructureElement structureElement, final int currentDepth, final ElementFinder nextElementFinder) throws Exception {
        currentElementFinder = nextElementFinder;
        if (currentElementFinder.isPredicate()) {
            SearchLocation searchLocation = currentElementFinder.lookupSearchLocation(structureElement, objectStore, false);
            if (searchLocation != null) {
                handleSearchLocation(searchLocation, structureElement, currentDepth);
            }
        }
    }

    public void text(final InternalStructureElement structureElement) throws Exception {
        if (currentOnTextLocation != null) {
            Predicate filter = currentOnTextLocation.getTextFilter();
            if (filter == null) {
                currentOnTextLocation.getOnTextHandler().onText(structureElement, objectStore);
            } else if (filter.evaluate(structureElement, objectStore)) {
                currentOnTextLocation.getOnTextHandler().onText(structureElement, objectStore);
            }
        }
    }

    public void endElement(final InternalStructureElement structureElement, final int currentDepth) throws Exception {
        while (parentDepth == currentDepth) {
            childDepth = parentDepth;
            parentDepth = depthStack.popAndPeek();
            currentOnTextLocation = null;
            ElementFinderStack.StackElement stackElement = elementFinderStack.pop();
            OnEndHandler onEndHandler = stackElement.getOnEndHandler();
            ElementFinder previousElementFinder = stackElement.getPreviousElementFinder();
            structureElement.clearCache();
            if (onEndHandler != null) {
                onEndHandler.endElement(structureElement, objectStore);
            }
            currentElementFinder = previousElementFinder;
        }
    }
}
