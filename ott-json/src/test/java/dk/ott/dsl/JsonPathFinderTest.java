package dk.ott.dsl; /**
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
import dk.ott.processing.ObjectStore;
import dk.ott.processing.ElementCursor;
import dk.ott.processing.JsonObservableTree;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JsonPathFinderTest {

    private static final JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(null);

    @Test
    public void singleValue() throws Exception {
        JsonObservableTree jsonPathFinder = new JsonObservableTree();
        TestingHandler testingHandler = new TestingHandler(false);
        jsonPathFinder.elementPath("/$/squadName").handle(testingHandler);
        jsonPathFinder.find(readFile());
    }

    @Test
    public void multipleSameDepthValue() throws Exception {
        JsonObservableTree jsonPathFinder = new JsonObservableTree();
        TestingHandler testingHandler = new TestingHandler(false);
        jsonPathFinder.elementPath("/$/squadName").handle(testingHandler);
        jsonPathFinder.elementPath("/$/formed").handle(testingHandler);
        jsonPathFinder.elementPath("/$/active").handle(testingHandler);
        ObjectStore objectStore = jsonPathFinder.find(readFile());

        System.out.println("ObjectStore object: " + objectStore.get("something", String.class));
        System.out.println("ObjectStore object: " + objectStore.get("something", Integer.class));
        System.out.println("ObjectStore object: " + objectStore.get("one", Number.class));
    }

    @Test
    public void arrayLookup() throws Exception {
        JsonObservableTree jsonPathFinder = new JsonObservableTree();
        TestingHandler testingHandler = new TestingHandler(false);
        jsonPathFinder.elementPath("/$/members/{}/name").handle(testingHandler);
        jsonPathFinder.find(readFile());
    }

    @Test
    public void arrayPredicateLookup() throws Exception {
        JsonObservableTree jsonPathFinder = new JsonObservableTree();
        TestingHandler testingHandler = new TestingHandler(false);
        jsonPathFinder.elementPath("/$/members/{}/name")
                .handle(testingHandler);
        jsonPathFinder.find(readFile());
    }

    @Test
    public void innerArrayLookup() throws Exception {
        JsonObservableTree jsonPathFinder = new JsonObservableTree();
        TestingHandler testingHandler = new TestingHandler(true);
        jsonPathFinder.elementPath("/$/members/{}/powers").handle(testingHandler);
        jsonPathFinder.find(readFile());
    }

    private String readFile() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream("example.json")));
        StringBuilder builder = new StringBuilder();
        while (reader.ready()) {
            builder.append(reader.readLine());
            builder.append("\n");
        }
        reader.close();
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    private class TestingHandler implements EventHandler {
        private boolean isArray;

        public TestingHandler(boolean isArray) {
            this.isArray = isArray;
        }

        @Override
        public void onStart(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
            objectStore.put("something", elementCursor.getElementName());
            objectStore.put("one", 123);
        }

        @Override
        public void onEnd(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
            System.out.println("End: " + elementCursor.getElementName());
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public void onText(ElementCursor elementCursor, ObjectStore objectStore) {
            System.out.println(String.format("%s=%s", elementCursor.getElementName(), elementCursor.getText()));
        }
    }
}
