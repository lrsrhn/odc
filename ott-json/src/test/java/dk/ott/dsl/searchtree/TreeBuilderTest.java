package dk.ott.dsl.searchtree; /**
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

import dk.ott.event.EventHandler;
import dk.ott.eventhandling.TextValueCollector;
import dk.ott.event.OnTextHandler;
import dk.ott.processing.ObjectStore;
import dk.ott.processing.ObservableTree;
import dk.ott.processing.ElementCursor;
import dk.ott.processing.JsonObservableTree;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dk.ott.predicate.Predicates.alwaysTrue;
import static dk.ott.predicate.Predicates.storedValue;
import static org.junit.Assert.assertEquals;

public class TreeBuilderTest {
    private JsonBuilderFactory jsonBuilderFactory;
    private ObservableTree observableTree;

    @Before
    public void init() {
        observableTree = new JsonObservableTree();
        jsonBuilderFactory = Json.createBuilderFactory(null);
    }

    @Test
    public void singleElementFinderForAll() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                    .add("one", jsonBuilderFactory.createObjectBuilder()
                        .add("two", jsonBuilderFactory.createObjectBuilder()
                            .add("three", "")
                        )
                    )
                .build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("$", "one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one", "$");

        observableTree.treeBuilder()
            .element("$").onStart().to(assertElementHandler)
                .element("one").onStart().to(assertElementHandler)
                    .element("two").onStart().to(assertElementHandler)
                        .element("three").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end(assertElementHandler)
            .end(assertElementHandler)
        .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

    @Test
    public void singleElementFinderIgnoreSomeElements() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                .add("one", jsonBuilderFactory.createObjectBuilder()
                    .add("faketwo", "")
                    .add("two", jsonBuilderFactory.createObjectBuilder()
                        .add("fakethree", "")
                        .add("three", jsonBuilderFactory.createObjectBuilder()
                            .add("four", ""))
                    )
                ).build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.treeBuilder()
                .element("$")
                    .element("one").onStart().to(assertElementHandler)
                        .element("two").onStart().to(assertElementHandler)
                            .element("three").onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end()
            .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

    @Test
    public void multipleElementFinderForAll() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                .add("one", jsonBuilderFactory.createObjectBuilder()
                    .add("two", jsonBuilderFactory.createObjectBuilder()
                        .add("three", "")
                        .add("four", "")
                    )
                ).build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observableTree.treeBuilder()
                .element("$")
                    .element("one").onStart().to(assertElementHandler)
                        .element("two").onStart().to(assertElementHandler)
                            .element("three").onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                            .element("four").onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end()
            .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

    @Test
    public void multipleElementFinderIgnoreSomeElements() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                .add("one", jsonBuilderFactory.createObjectBuilder()
                    .add("faketwo", "")
                    .add("two", jsonBuilderFactory.createObjectBuilder()
                        .add("fakethree", "")
                        .add("three", jsonBuilderFactory.createObjectBuilder()
                            .add("five", "")
                        )
                        .add("four", "")
                        )
                    .add("six", "")
                ).build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observableTree.treeBuilder()
                .element("$")
                    .element("one").onStart().to(assertElementHandler)
                        .element("two").onStart().to(assertElementHandler)
                            .element("three").onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                            .element("four").onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end()
            .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

    @Test
    public void singleElementFinderForAllWithFilter() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                .add("one", jsonBuilderFactory.createObjectBuilder()
                    .add("two", jsonBuilderFactory.createObjectBuilder()
                        .add("three", "")
                    )
                ).build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.treeBuilder()
                .element("$")
                    .element("one").onStart().filter(alwaysTrue()).to(assertElementHandler)
                        .element("two").onStart().filter(alwaysTrue()).to(assertElementHandler)
                            .element("three").onStart().filter(alwaysTrue()).to(assertElementHandler)
                            .end(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                .end()
            .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

    @Test
    public void multipleElementFinderForAllWithPredicate() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                .add("one", jsonBuilderFactory.createObjectBuilder()
                    .add("two", jsonBuilderFactory.createObjectBuilder()
                        .add("att", "one")
                        .add("three", "")
                        .add("four", "")
                    )
                ).build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observableTree.treeBuilder()
                .element("$")
                    .element("one").onStart().to(assertElementHandler)
                        .element("two").onStart().to(assertElementHandler)
                            .element("att")
                                .onText().to(new TextValueCollector("att"))
                            .end()
                            .element("three")
                                .predicate(storedValue("att", "one"))
                                    .onStart().to(assertElementHandler)
                                .end(assertElementHandler)
                            .end()
                            .element("four").onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end(assertElementHandler)
                    .end(assertElementHandler)
                    .end()
            .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }



    @Test
    public void multipleRelative() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                .add("one", jsonBuilderFactory.createObjectBuilder()
                    .add("two", jsonBuilderFactory.createObjectBuilder()
                        .add("three", "")
                    )
                    .add("four", jsonBuilderFactory.createObjectBuilder()
                        .add("five", "")
                    )
                ).build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("three", "five");
        assertElementHandler.exptectedEndElements("three", "five");

        observableTree.treeBuilder()
                .element("$")
                    .element("one")
                        .element("two")
                            .element("three").onStart().to(assertElementHandler)
                            .end(assertElementHandler)
                        .end()
                        .relativeElement("five").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end()
                .end()
                .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

    @Test
    public void singleRelative() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                .add("one", jsonBuilderFactory.createObjectBuilder()
                    .add("four", jsonBuilderFactory.createObjectBuilder()
                        .add("five", "")
                    )
                ).build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("five");
        assertElementHandler.exptectedEndElements("five");

        observableTree.treeBuilder()
                .element("$")
                    .element("one")
                        .relativeElement("five").onStart().to(assertElementHandler)
                        .end(assertElementHandler)
                    .end()
                .end()
                .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

    @Test
    public void testRecursion() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                .add("one", jsonBuilderFactory.createObjectBuilder()
                    .add("two", jsonBuilderFactory.createObjectBuilder()
                        .add("three", "")
                        .add("one", jsonBuilderFactory.createObjectBuilder()
                            .add("two", jsonBuilderFactory.createObjectBuilder()
                                .add("three", "")
                            )
                        )
                        .add("five", "")
                    )
                ).build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "one", "two", "three", "five");
        assertElementHandler.exptectedEndElements("three", "three", "two", "one", "five", "two", "one");

        String referenceName = "numberone";
        observableTree.treeBuilder()
                .element("$")
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
                .end()
            .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

    @Test
    public void allFinder() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                .add("one", jsonBuilderFactory.createObjectBuilder()
                    .add("two", jsonBuilderFactory.createObjectBuilder()
                        .add("three", "")
                    )
                    .add("four", jsonBuilderFactory.createObjectBuilder()
                       .add("five", "")
                    )
                )
                .build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();

        assertElementHandler.exptectedStartElements("two", "three", "four", "five");
        assertElementHandler.exptectedEndElements("three", "two", "five", "four");

        observableTree.treeBuilder()
                .element("$")
                    .element("one")
                        .all(assertElementHandler, null, assertElementHandler)
                    .end()
                .end()
                .build();

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

@Test
    public void valueTest() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                    .addNull("isnull")
                    .add("isinteger", 2)
                    .add("islong", 3L)
                    .add("isdouble", 2.3)
                    .add("isboolean", true)
                    .add("isstring", "text")
                .build()
        );

        OnTextHandler valueTester = new OnTextHandler() {
            @Override
            public void onText(ElementCursor structureElement, ObjectStore objectStore) {
                String value = structureElement.getText();
                if(value == null) {
                    assertEquals("isnull", structureElement.getElementName());
                } else if ("2".equals(value)) {
                    assertEquals("isinteger", structureElement.getElementName());
                } else if ("3".equals(value)) {
                    assertEquals("islong", structureElement.getElementName());
                } else if ("2.3".equals(value)) {
                    assertEquals("isdouble", structureElement.getElementName());
                } else if ("true".equals(value)) {
                    assertEquals("isboolean", structureElement.getElementName());
                } else if ("text".equals(value)) {
                    assertEquals("isstring", structureElement.getElementName());
                } else {
                    Assert.fail("Unknwon value. " + structureElement.getElementName() + ": " + value);
                }
            }
        };

        observableTree.treeBuilder()
            .element("$")
                .element("isnull").onText().to(valueTester)
                .end()
                .element("isinteger").onText().to(valueTester)
                .end()
                .element("islong").onText().to(valueTester)
                .end()
                .element("isdouble").onText().to(valueTester)
                .end()
                .element("isboolean").onText().to(valueTester)
                .end()
                .element("isstring").onText().to(valueTester)
                .end()
            .end()
            .build();

        observableTree.find(new StringReader(jsonText));
    }

    private String jsonStructureToString(JsonStructure jsonStructure) {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(writer);
        jsonWriter.write(jsonStructure);
        jsonWriter.close();
        return writer.toString();
    }

    private static class AssertEventHandler implements EventHandler {
        private List<String> expectedStartElements;
        private List<String> expectedEndElements;
        private List<String> startElementsActual;
        private List<String> endElementsActual;

        public AssertEventHandler() {
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
            assertEquals(expectedStartElements, startElementsActual);
            assertEquals(expectedEndElements, endElementsActual);
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

        }
    }
}
