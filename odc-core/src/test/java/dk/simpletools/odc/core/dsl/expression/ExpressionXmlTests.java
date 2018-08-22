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
package dk.simpletools.odc.core.dsl.expression;

import dk.simpletools.odc.core.predicate.Predicates;
import dk.simpletools.odc.core.processing.*;
import dk.simpletools.odc.core.finder.ElementHandler;
import dk.simpletools.odc.core.xml.builder.XmlStreamBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public abstract class ExpressionXmlTests {
    protected ObservablePathFinder observablePathFinder;

    @Before
    public abstract void setObservablePathFinder();

    @Test
    public void singleElementFinderForAll() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
            .element("one")
                .element("two")
                    .element("three")
                    .elementEnd()
                .elementEnd()
            .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observablePathFinder.addXpath("/one").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/three").handleElementBy(assertElementHandler);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void textTest() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
                .element("one")
                    .element("two")
                        .element("three")
                            .value("Something darkside")
                        .elementEnd()
                    .elementEnd()
                .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.expectedValue("three", "Something darkside");
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observablePathFinder.addXpath("/one").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/three").handleElementBy(assertElementHandler);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void singleElementFinderIgnoreSomeElements() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
            .element("one")
                .element("faketwo")
                .elementEnd()
                .element("two")
                    .element("fakethree")
                    .elementEnd()
                    .element("three")
                        .element("four")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observablePathFinder.addXpath("/one").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/three").handleElementBy(assertElementHandler);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void multipleElementFinderForAll() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
            .element("one")
                .element("two")
                    .element("three")
                    .elementEnd()
                    .element("four")
                    .elementEnd()
                .elementEnd()
            .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observablePathFinder.addXpath("/one").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/three").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/four").handleElementBy(assertElementHandler);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void multipleElementFinderIgnoreSomeElements() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
            .element("one")
                .element("faketwo")
                .elementEnd()
                .element("two")
                    .element("fakethree")
                    .elementEnd()
                    .element("three")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                    .element("four")
                    .elementEnd()
                .elementEnd()
                .element("six")
                .elementEnd()
            .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observablePathFinder.addXpath("/one").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/three").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/four").handleElementBy(assertElementHandler);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void singleElementFinderForAllWithFilter() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
            .element("one")
                .element("two")
                    .element("three")
                    .elementEnd()
                .elementEnd()
            .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observablePathFinder.addXpath("/one").filter(Predicates.alwaysTrue()).handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").filter(Predicates.alwaysTrue()).handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/three").filter(Predicates.alwaysTrue()).handleElementBy(assertElementHandler);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void multipleElementFinderForAllWithPredicate() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
            .element("one")
                .element("two")
                    .element("three").attribute("att", "one")
                    .elementEnd()
                    .element("four")
                    .elementEnd()
                .elementEnd()
            .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observablePathFinder.addXpath("/one").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/three").predicate(Predicates.attribute("att", "one")).handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two/four").handleElementBy(assertElementHandler);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void multiplePredicate() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
            .element("one")
                .element("two").attribute("att", "bla")
                    .element("three")
                    .elementEnd()
                .elementEnd()
                .element("two").attribute("att", "wee")
                    .element("five")
                    .elementEnd()
                .elementEnd()
            .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "two", "five");
        assertElementHandler.exptectedEndElements("three", "two", "five", "two", "one");

        observablePathFinder.addXpath("/one").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").predicate(Predicates.attribute("att", "bla")).elementsAbsolute("three").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").predicate(Predicates.attribute("att", "wee")).elementsAbsolute("five").handleElementBy(assertElementHandler);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void singlePredicateNoMatch() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
            .element("one")
                .element("two").attribute("att", "bla")
                    .element("three")
                    .elementEnd()
                .elementEnd()
                .element("two").attribute("att", "wee")
                    .element("five")
                    .elementEnd()
                .elementEnd()
            .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "two", "five");
        assertElementHandler.exptectedEndElements("three", "two", "five", "two", "one");

        observablePathFinder.addXpath("/one").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").predicate(Predicates.attribute("att", "bla")).elementsAbsolute("three").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").predicate(Predicates.attribute("att", "wee")).elementsAbsolute("five").handleElementBy(assertElementHandler);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void testRecursion() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
            .element("one")
                .element("two").attribute("att", "bla")
                    .element("three")
                    .elementEnd()
                    .element("one")
                        .element("two").attribute("att", "bla")
                            .element("three")
                            .elementEnd()
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
                .element("two").attribute("att", "wee")
                    .element("five")
                    .elementEnd()
                .elementEnd()
            .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "one", "two", "three", "two", "five");
        assertElementHandler.exptectedEndElements("three", "three", "two", "one", "two", "five", "two", "one");

        PathFragment recursion = observablePathFinder.addXpath("/one").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").elementsAbsolute("three").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").elementsAbsolute("five").handleElementBy(assertElementHandler);
        observablePathFinder.addXpath("/one/two").recursion(recursion);

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    private XmlStreamBuilder withXmlBuilder(StringBuilder builder) {
        return new XmlStreamBuilder(builder);
    }

    private static class AssertElementHandler implements ElementHandler {
        private List<String> expectedStartElements;
        private List<String> expectedEndElements;
        private List<String> startElementsActual;
        private List<String> endElementsActual;
        private Map<String, String> elementsToRead;

        public AssertElementHandler() {
            this.startElementsActual = new ArrayList<String>();
            this.endElementsActual = new ArrayList<String>();
            this.elementsToRead = new HashMap<String, String>();
        }

        public void exptectedStartElements(String...elements) {
            this.expectedStartElements = Arrays.asList(elements);
        }

        public void exptectedEndElements(String...elements) {
            this.expectedEndElements = Arrays.asList(elements);
        }

        public void verify() {
            Assert.assertEquals(expectedStartElements, startElementsActual);
            Assert.assertEquals(expectedEndElements, endElementsActual);
        }

        @Override
        public void startElement(StructureElement structureElement) throws Exception {
            startElementsActual.add(structureElement.getElementName());
            String expectedValue = elementsToRead.get(structureElement.getElementName());
            if (expectedValue != null) {
                Assert.assertEquals(expectedValue, structureElement.getElementValue());
            }
        }

        @Override
        public void endElement(EndElement endElement, ValueStore valueStore, ObjectStore objectStore) throws Exception {
            endElementsActual.add(endElement.getElementName());
        }

        @Override
        public void clear() {

        }

        public void expectedValue(String key, String value) {
            elementsToRead.put(key, value);
        }
    }
}
