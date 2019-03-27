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
package dk.ott.core.dsl.searchtree;

import dk.ott.core.finder.ElementHandler;
import dk.ott.core.finder.OnStartHandler;
import dk.ott.core.finder.OnTextHandler;
import dk.ott.core.predicate.Predicates;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.ObservablePathFinder;
import dk.ott.core.processing.StructureElement;
import dk.ott.core.xml.builder.XmlStreamBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TreeBuilderTests {
    protected ObservablePathFinder observablePathFinder;

    @Before
    public abstract void setObservablePathFinder();

    // @formatter:off
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

        observablePathFinder.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

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

        observablePathFinder.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

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

        observablePathFinder.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                        .element("four").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

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

        observablePathFinder.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                        .element("four").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

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

        observablePathFinder.treeBuilder()
                .element("one").onStart().filter(Predicates.alwaysTrue()).to(assertElementHandler)
                    .element("two").onStart().filter(Predicates.alwaysTrue()).to(assertElementHandler)
                        .element("three").onStart().filter(Predicates.alwaysTrue()).to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

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

        observablePathFinder.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three")
                            .predicate(Predicates.attribute("att", "one"))
                                .onStart().to(assertElementHandler)
                            .end()
                        .end(assertElementHandler)
                        .element("four").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

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

        observablePathFinder.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .predicate(Predicates.attribute("att", "bla"))
                            .element("three")
                                .onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end()
                        .predicate(Predicates.attribute("att", "wee"))
                            .element("five")
                                .onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end()
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

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

        observablePathFinder.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .predicate(Predicates.attribute("att", "bla"))
                            .element("three")
                                .onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end()
                        .predicate(Predicates.attribute("att", "wee"))
                            .element("five")
                                .onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end()
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }


    @Test
    public void multipleRelative() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
                .element("one")
                    .element("two")
                        .element("three")
                        .elementEnd()
                    .elementEnd()
                    .element("four")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("three", "five");
        assertElementHandler.exptectedEndElements("three", "five");

        observablePathFinder.treeBuilder()
                .element("one")
                    .element("two")
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end()
                    .relativeElement("five").onStart().to(assertElementHandler)
                    .end(assertElementHandler)
                .end()
                .build();

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void singleRelative() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
                .element("one")
                    .element("four")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("five");
        assertElementHandler.exptectedEndElements("five");

        observablePathFinder.treeBuilder()
                .element("one")
                    .relativeElement("five").onStart().to(assertElementHandler)
                    .end(assertElementHandler)
                .end()
                .build();

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void multipleRoot() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
                .element("one")
                    .element("four")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("five");
        assertElementHandler.exptectedEndElements("five");

        observablePathFinder.treeBuilder()
                .element("two")
                .end()
                .element("one")
                    .relativeElement("five").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                .end()
                .build();

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void onText() throws IOException {
        StringBuilder builder = new StringBuilder();
        withXmlBuilder(builder)
                .element("one")
                    .element("four")
                        .element("five")
                            .value("This is the value")
                        .elementEnd()
                    .elementEnd()
                    .element("two")
                    .elementShortEnd()
                .elementEnd();

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("five");
        assertElementHandler.exptectedEndElements("five");

        observablePathFinder.treeBuilder()
                .element("one")
                    .relativeElement("five").onStart().to(assertElementHandler)
                        .onText().to(assertElementHandler)
                    .end(assertElementHandler)
                    .element("two")
                        .onText().to(assertElementHandler)
                    .end()
                .end()
                .build();

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

        String referenceName = "numberone";
        observablePathFinder.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .storeReference(referenceName)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three")
                            .onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                        .element("five")
                            .onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                        .recursionToReference(referenceName)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    @Test
    public void allFinder() throws IOException {
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

        assertElementHandler.exptectedStartElements("two", "three", "two", "five");
        assertElementHandler.exptectedEndElements("three", "two", "five", "two");

        observablePathFinder.treeBuilder()
                .element("")
                .end()
                .element("")
                .end()
                .element("one")
                    .all(assertElementHandler, assertElementHandler)
                .end()
                .build();

        observablePathFinder.find(new StringReader(builder.toString()));
        assertElementHandler.verify();
    }

    /**
     *
     * Please prettify this test.
     */
    @Test
    public void testRaw() throws IOException {
        StringBuilder builder = new StringBuilder();
        XmlStreamBuilder xmlBuilder = withXmlBuilder(builder)
                .element("one");
        final String longValue = veryLongValue();

        for (int i = 0; i < 10000; i++) {
            xmlBuilder.element("two")
                    .valueNoEscaping(longValue)
                    .elementEnd();
        }
        xmlBuilder.elementEnd();

        observablePathFinder.treeBuilder()
                .element("one")
                    .element("two")
                        .onStart().to(new OnStartHandler() {
            @Override
            public void startElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
                Assert.assertEquals(longValue.length(), structureElement.getRawElementValue().length());
                Assert.assertEquals(longValue, structureElement.getRawElementValue());
            }
        });

        observablePathFinder.find(new StringReader(builder.toString()));

    }

    private String veryLongValue() {
        String value = "adcadca¤%¤%¤%¤¤WEFSD<QEDFAS/>";
        StringBuilder builder = new StringBuilder(10000);
        for (int i = 0; i < 500; i++) {
            builder.append(value);
        }
        return builder.toString();

    }

    // @formatter:on

    private XmlStreamBuilder withXmlBuilder(StringBuilder builder) {
        return new XmlStreamBuilder(builder);
    }

    private static class AssertElementHandler implements ElementHandler, OnTextHandler {
        private List<String> expectedStartElements;
        private List<String> expectedEndElements;
        private List<String> startElementsActual;
        private List<String> endElementsActual;

        public AssertElementHandler() {
            this.startElementsActual = new ArrayList<String>();
            this.endElementsActual = new ArrayList<String>();
        }

        public void exptectedStartElements(String... elements) {
            this.expectedStartElements = Arrays.asList(elements);
        }

        public void exptectedEndElements(String... elements) {
            this.expectedEndElements = Arrays.asList(elements);
        }

        public void verify() {
            Assert.assertEquals(expectedStartElements, startElementsActual);
            Assert.assertEquals(expectedEndElements, endElementsActual);
        }

        @Override
        public void startElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
            startElementsActual.add(structureElement.getElementName());
        }

        @Override
        public void endElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
            endElementsActual.add(structureElement.getElementName());
        }


        @Override
        public void onText(StructureElement structureElement, ObjectStore objectStore) {
            System.out.println(structureElement.getText());
        }
    }
}
