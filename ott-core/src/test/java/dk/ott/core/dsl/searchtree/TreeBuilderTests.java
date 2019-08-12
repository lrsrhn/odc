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

import dk.ott.core.dsl.AssertEventHandler;
import dk.ott.core.dsl.ObservableRootTreeFragment;
import dk.ott.core.event.OnTextHandler;
import dk.ott.core.predicate.Predicates;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.ObservableTree;
import dk.ott.core.processing.ElementCursor;
import dk.ott.xml.XmlStreamBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static dk.ott.xml.XmlStreamBuilderFactory.createXmlStreamBuilderToString;

public abstract class TreeBuilderTests {
    protected ObservableTree observableTree;

    @Before
    public abstract void setObservablePathFinder();

    // @formatter:off
    @Test
    public void singleElementFinderForAll() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two")
                        .element("three")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void mixedContentTest() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two")
                        .valueNoEscaping("This is <p>pure</p> evil<break/><three></three>")
                    .elementEnd()
                    .element("two")
                        .value("but still nice")
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "two");
        assertElementHandler.exptectedEndElements("three", "two", "two", "one");
        assertElementHandler.exptectedTexts("This is", "but still nice");

        observableTree.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .onText().to(assertElementHandler)
                            .element("three").onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void singleElementFinderIgnoreSomeElements() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
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
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void multipleElementFinderForAll() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two")
                        .element("three")
                        .elementEnd()
                        .element("four")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observableTree.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                        .element("four").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void multipleElementFinderIgnoreSomeElements() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
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
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observableTree.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                        .element("four").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void singleElementFinderForAllWithFilter() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two")
                        .element("three")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.treeBuilder()
                .element("one").onStart().filter(Predicates.alwaysTrue()).to(assertElementHandler)
                    .element("two").onStart().filter(Predicates.alwaysTrue()).to(assertElementHandler)
                        .element("three").onStart().filter(Predicates.alwaysTrue()).to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void multipleElementFinderForAllWithPredicate() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two")
                        .element("three").attribute("att", "one")
                        .elementEnd()
                        .element("four")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observableTree.treeBuilder()
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

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void multiplePredicate() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two").attribute("att", "bla")
                        .element("three")
                        .elementEnd()
                    .elementEnd()
                    .element("two").attribute("att", "wee")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "two", "five");
        assertElementHandler.exptectedEndElements("three", "two", "five", "two", "one");

        observableTree.treeBuilder()
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

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void singlePredicateNoMatch() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two").attribute("att", "bla")
                        .element("three")
                        .elementEnd()
                    .elementEnd()
                    .element("two").attribute("att", "wee")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "two", "five");
        assertElementHandler.exptectedEndElements("three", "two", "five", "two", "one");

        observableTree.treeBuilder()
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

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }


    @Test
    public void multipleRelative() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two")
                        .element("three")
                        .elementEnd()
                    .elementEnd()
                    .element("four")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("three", "five");
        assertElementHandler.exptectedEndElements("three", "five");

        observableTree.treeBuilder()
                .element("one")
                    .element("two")
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end()
                    .relativeElement("five").onStart().to(assertElementHandler)
                    .end(assertElementHandler)
                .end()
                .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void singleRelative() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("four")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("five");
        assertElementHandler.exptectedEndElements("five");

        observableTree.treeBuilder()
                .element("one")
                    .relativeElement("five").onStart().to(assertElementHandler)
                    .end(assertElementHandler)
                .end()
                .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void multipleRoot() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("four")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("five");
        assertElementHandler.exptectedEndElements("five");

        observableTree.treeBuilder()
                .element("two")
                .end()
                .element("one")
                    .relativeElement("five").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                .end()
                .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void onText() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("four")
                        .element("five")
                            .value("This is the value")
                        .elementEnd()
                    .elementEnd()
                    .elementShort("two")
                    .element("three")
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("five");
        assertElementHandler.exptectedEndElements("five");
        assertElementHandler.exptectedTexts("This is the value");

        observableTree.treeBuilder()
                .element("one")
                    .element("two")
                        .onText().to(assertElementHandler)
                    .end()
                    .element("three")
                        .onText().to(assertElementHandler)
                    .end()
                    .relativeElement("five").onStart().to(assertElementHandler)
                        .onText().to(assertElementHandler)
                    .end(assertElementHandler)
                .end()
                .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void testRecursion() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
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
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "one", "two", "three", "two", "five");
        assertElementHandler.exptectedEndElements("three", "three", "two", "one", "two", "five", "two", "one");

        String referenceName = "numberone";
        observableTree.treeBuilder()
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

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void allFinder() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two").attribute("att", "bla")
                        .element("three")
                        .elementEnd()
                    .elementEnd()
                    .element("two").attribute("att", "wee")
                        .element("five")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();

        assertElementHandler.exptectedStartElements("two", "three", "two", "five");
        assertElementHandler.exptectedEndElements("three", "two", "five", "two");

        observableTree.treeBuilder()
                .element("")
                .end()
                .element("")
                .end()
                .element("one")
                    .all(assertElementHandler, null, assertElementHandler)
                .end()
                .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void mixingTreeBuilderWithExpressions() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
            .element("one")
                .element("two").attribute("att", "bla")
                    .element("three")
                    .elementEnd()
                .elementEnd()
                .element("two").attribute("att", "wee")
                    .element("five")
                    .elementEnd()
                .elementEnd()
            .elementEnd()
        .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "two", "five");
        assertElementHandler.exptectedEndElements("three", "two", "five", "two", "one");

        observableTree.treeBuilder()
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .addTreeFragment(predicateForThree(assertElementHandler))
                        .predicate(Predicates.attribute("att", "wee"))
                            .element("five")
                                .onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end()
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .build();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    private ObservableRootTreeFragment predicateForThree(AssertEventHandler assertElementHandler) {
        ObservableRootTreeFragment subTree = ObservableTree.detachedTree();
        subTree.predicateExp(Predicates.attribute("att", "bla")).elementPath("/three").handle(assertElementHandler);
        return subTree;
    }

    /**
     *
     * Please prettify this test.
     */
    @Test
    public void testRaw() throws Exception {
        XmlStreamBuilder xmlBuilder = createXmlStreamBuilderToString(true);
        xmlBuilder.element("one");
        final String longValue = veryLongValue();

        for (int i = 0; i < 10000; i++) {
            xmlBuilder.element("two")
                    .valueNoEscaping(longValue)
                .elementEnd();
        }
        xmlBuilder.elementEnd();

        observableTree.treeBuilder()
                .elementPath("/one/two")
                        .onText().asRaw().to(new OnTextHandler() {
            @Override
            public void onText(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
                Assert.assertEquals(longValue.length(), elementCursor.getText().length());
                Assert.assertEquals(longValue, elementCursor.getText());
            }
        });

        observableTree.find(new StringReader(xmlBuilder.toString()));

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

}
