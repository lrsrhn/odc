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
package dk.ott.dsl;

import dk.ott.event.EventHandler;
import dk.ott.processing.ObjectStore;
import dk.ott.processing.ElementCursor;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssertEventHandler implements EventHandler {
    private List<String> expectedStartElements;
    private List<String> expectedEndElements;
    private List<String> startElementsActual;
    private List<String> endElementsActual;
    private List<String> expectedTexts;
    private List<String> textsActual;

    public AssertEventHandler() {
        this.startElementsActual = new ArrayList<String>();
        this.endElementsActual = new ArrayList<String>();
        this.textsActual = new ArrayList<String>();
        this.expectedTexts = new ArrayList<String>();
    }

    public void exptectedStartElements(String... elements) {
        this.expectedStartElements = Arrays.asList(elements);
    }

    public void exptectedEndElements(String... elements) {
        this.expectedEndElements = Arrays.asList(elements);
    }

    public void exptectedTexts(String... texts) {
        this.expectedTexts = Arrays.asList(texts);
    }

    public void verify() {
        Assert.assertEquals(expectedStartElements, startElementsActual);
        Assert.assertEquals(expectedEndElements, endElementsActual);
        Assert.assertEquals(expectedTexts, textsActual);
    }

    @Override
    public void onStart(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
        startElementsActual.add(elementCursor.getElementName());
    }

    @Override
    public void onEnd(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
        endElementsActual.add(elementCursor.getElementName());
    }


    @Override
    public void onText(ElementCursor elementCursor, ObjectStore objectStore) {
        this.textsActual.add(elementCursor.getText());
    }
}
