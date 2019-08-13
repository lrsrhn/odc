package dk.ott.dsl.expression; /**
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

import dk.ott.dsl.ObservableTreeFragment;
import dk.ott.event.EventHandler;
import dk.ott.predicate.Predicates;
import dk.ott.processing.ObjectStore;
import dk.ott.processing.ObservableTree;
import dk.ott.processing.ElementCursor;
import dk.ott.standardhandlers.EventForwarderBuilder;
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
import java.util.*;

public class ExpressionXmlTest {
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
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.elementPath("/$/one").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/three").handle(assertElementHandler);

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
    }

    @Test
    public void textTest() throws IOException {
        String jsonText = jsonStructureToString(
            jsonBuilderFactory
                .createObjectBuilder()
                    .add("one", jsonBuilderFactory.createObjectBuilder()
                        .add("two", jsonBuilderFactory.createObjectBuilder()
                            .add("three", "Something darkside")
                        )
                    )
                .build()
        );

        AssertEventHandler assertElementHandler = new AssertEventHandler();
        assertElementHandler.expectedValue("three", "Something darkside");
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.elementPath("/$/one").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/three").handle(assertElementHandler);

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

        observableTree.elementPath("/$/one").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/three").handle(assertElementHandler);

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

        observableTree.elementPath("/$/one").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/three").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/four").handle(assertElementHandler);

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

        observableTree.elementPath("/$/one").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/three").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/four").handle(assertElementHandler);

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

        observableTree.elementPath("/$/one").filter(Predicates.alwaysTrue()).handle(assertElementHandler);
        observableTree.elementPath("/$/one/two").filter(Predicates.alwaysTrue()).handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/three").filter(Predicates.alwaysTrue()).handle(assertElementHandler);

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

        observableTree.elementPath("/$/one").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/att").onText(EventForwarderBuilder.builder().textValueCollector("att").build());
        observableTree.elementPath("/$/one/two/three").predicate(Predicates.storedValue("att", "one")).handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/four").handle(assertElementHandler);

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

        ObservableTreeFragment recursion = observableTree.elementPath("/$/one").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/three").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two/five").handle(assertElementHandler);
        observableTree.elementPath("/$/one/two").recursion(recursion);

        observableTree.find(new StringReader(jsonText));
        assertElementHandler.verify();
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
        private Map<String, String> elementsToRead;

        public AssertEventHandler() {
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
        public void onStart(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
            startElementsActual.add(elementCursor.getElementName());
        }

        @Override
        public void onEnd(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
            endElementsActual.add(elementCursor.getElementName());
        }

        public void expectedValue(String key, String value) {
            elementsToRead.put(key, value);
        }

        @Override
        public void onText(ElementCursor elementCursor, ObjectStore objectStore) {
            String expectedValue = elementsToRead.get(elementCursor.getElementName());
            if (expectedValue != null) {
                Assert.assertEquals(expectedValue, elementCursor.getText());
            }
        }
    }
}