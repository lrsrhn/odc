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
package dk.ott.dsl.expression;

import dk.ott.dsl.AssertEventHandler;
import dk.ott.dsl.ObservableTreeFragment;
import dk.ott.event.OnTextHandler;
import dk.ott.predicate.Predicates;
import dk.ott.processing.ObjectStore;
import dk.ott.processing.ObservableTree;
import dk.ott.processing.ElementCursor;
import dk.ott.xml.XmlStreamBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static dk.ott.xml.XmlStreamBuilderFactory.createXmlStreamBuilderToString;

public abstract class ExpressionXmlTests {
    protected ObservableTree observableTree;

    @Before
    public abstract void setObservablePathFinder();

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

        observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.elementPath("/one/two/three").handle(assertElementHandler);
        observableTree.dereferenceSearchTree();

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void textTest() throws Exception {
        String xml = createXmlStreamBuilderToString(true)
                .element("one")
                    .element("two")
                        .element("three")
                            .value("Something darkside")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedTexts("Something darkside");
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.elementPath("/one/two/three").handle(assertElementHandler);

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

        observableTree.elementPath("/one/four/five").handle(assertElementHandler);
        observableTree.elementPath("/two/four/five").handle(assertElementHandler);

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

        observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.elementPath("/one/two/three").handle(assertElementHandler);

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

        observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.elementPath("/one/two/three").handle(assertElementHandler);
        observableTree.elementPath("/one/two/four").handle(assertElementHandler);

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

        observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.elementPath("/one/two/three").handle(assertElementHandler);
        observableTree.elementPath("/one/two/four").handle(assertElementHandler);

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

        observableTree.elementPath("/one").filter(Predicates.alwaysTrue()).handle(assertElementHandler);
        observableTree.elementPath("/one/two").filter(Predicates.alwaysTrue()).handle(assertElementHandler);
        observableTree.elementPath("/one/two/three").filter(Predicates.alwaysTrue()).handle(assertElementHandler);

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

        observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.elementPath("/one/two/three").predicate(Predicates.attribute("att", "one")).handle(assertElementHandler);
        observableTree.elementPath("/one/two/four").handle(assertElementHandler);

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

        observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.elementPath("/one/two").predicate(Predicates.attribute("att", "bla")).elementPath("/three").handle(assertElementHandler);
        observableTree.elementPath("/one/two").predicate(Predicates.attribute("att", "wee")).elementPath("/five").handle(assertElementHandler);

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

        observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.elementPath("/one/two").predicate(Predicates.attribute("att", "bla")).elementPath("/three").handle(assertElementHandler);
        observableTree.elementPath("/one/two").predicate(Predicates.attribute("att", "wee")).elementPath("/five").handle(assertElementHandler);

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

    @Test
    public void mixingExpressionsWithTreebuilder() throws Exception {
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

        observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.addSubTree(
            ObservableTree.detachedTree()
                .element("one")
                    .element("two")
                        .predicate(Predicates.attribute("att", "bla"))
                            .element("three")
                                .onStart().to(assertElementHandler)
                                .onText().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end()
                    .end()
                .end()
            .end()
        );
        observableTree.elementPath("/one/two").predicate(Predicates.attribute("att", "wee")).elementPath("/five").handle(assertElementHandler);

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

        ObservableTreeFragment recursion = observableTree.elementPath("/one").handle(assertElementHandler);
        observableTree.elementPath("/one/two").handle(assertElementHandler);
        observableTree.elementPath("/one/two/three").handle(assertElementHandler);
        observableTree.elementPath("/one/two/five").handle(assertElementHandler);
        observableTree.elementPath("/one/two").recursion(recursion);

        observableTree.find(new StringReader(xml));
        assertElementHandler.verify();
    }

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

        observableTree.elementPath("/one/two").onRawText(new OnTextHandler() {
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

}
