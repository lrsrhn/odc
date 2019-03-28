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

import dk.ott.core.dsl.ObservableTreeFragment;
import dk.ott.core.finder.ElementHandler;
import dk.ott.core.predicate.Predicates;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.ObservableTree;
import dk.ott.core.processing.StructureElement;
import dk.ott.core.standardhandlers.EventForwarderBuilder;
import dk.ott.json.JsonObservableTree;
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

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.addXpath("/$/one").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/three").handle(assertElementHandler);

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

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.expectedValue("three", "Something darkside");
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.addXpath("/$/one").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/three").handle(assertElementHandler);

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

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.addXpath("/$/one").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/three").handle(assertElementHandler);

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

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observableTree.addXpath("/$/one").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/three").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/four").handle(assertElementHandler);

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

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observableTree.addXpath("/$/one").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/three").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/four").handle(assertElementHandler);

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

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three");
        assertElementHandler.exptectedEndElements("three", "two", "one");

        observableTree.addXpath("/$/one").filter(Predicates.alwaysTrue()).handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").filter(Predicates.alwaysTrue()).handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/three").filter(Predicates.alwaysTrue()).handle(assertElementHandler);

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

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "four");
        assertElementHandler.exptectedEndElements("three", "four", "two", "one");

        observableTree.addXpath("/$/one").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/att").onText(EventForwarderBuilder.builder().textValueCollector("att").build());
        observableTree.addXpath("/$/one/two/three").predicate(Predicates.storedValue("att", "one")).handle(assertElementHandler);
        observableTree.addXpath("/$/one/two/four").handle(assertElementHandler);

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

        AssertElementHandler assertElementHandler = new AssertElementHandler();
        assertElementHandler.exptectedStartElements("one", "two", "three", "one", "two", "three", "five");
        assertElementHandler.exptectedEndElements("three", "three", "two", "one", "five", "two", "one");

        ObservableTreeFragment recursion = observableTree.addXpath("/$/one").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").elementsAbsolute("three").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").elementsAbsolute("five").handle(assertElementHandler);
        observableTree.addXpath("/$/one/two").recursion(recursion);

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
        public void startElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
            startElementsActual.add(structureElement.getElementName());
        }

        @Override
        public void endElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
            endElementsActual.add(structureElement.getElementName());
        }

        public void expectedValue(String key, String value) {
            elementsToRead.put(key, value);
        }

        @Override
        public void onText(StructureElement structureElement, ObjectStore objectStore) {
            String expectedValue = elementsToRead.get(structureElement.getElementName());
            if (expectedValue != null) {
                Assert.assertEquals(expectedValue, structureElement.getText());
            }
        }
    }
}