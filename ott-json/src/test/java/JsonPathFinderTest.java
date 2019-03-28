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

import dk.ott.core.finder.ElementHandler;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.StructureElement;
import dk.ott.json.JsonTreeTraverser;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JsonPathFinderTest {

    private static final JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(null);

    @Test
    public void singleValue() throws Exception {
        JsonTreeTraverser jsonPathFinder = new JsonTreeTraverser();
        TestingHandler testingHandler = new TestingHandler(false);
        jsonPathFinder.addXpath("/$/squadName").handle(testingHandler);
        jsonPathFinder.find(readFile());
    }

    @Test
    public void multipleSameDepthValue() throws Exception {
        JsonTreeTraverser jsonPathFinder = new JsonTreeTraverser();
        TestingHandler testingHandler = new TestingHandler(false);
        jsonPathFinder.addXpath("/$/squadName").handle(testingHandler);
        jsonPathFinder.addXpath("/$/formed").handle(testingHandler);
        jsonPathFinder.addXpath("/$/active").handle(testingHandler);
        ObjectStore objectStore = jsonPathFinder.find(readFile());

        System.out.println("ObjectStore object: " + objectStore.get("something", String.class));
        System.out.println("ObjectStore object: " + objectStore.get("something", Integer.class));
        System.out.println("ObjectStore object: " + objectStore.get("one", Number.class));
    }

    @Test
    public void arrayLookup() throws Exception {
        JsonTreeTraverser jsonPathFinder = new JsonTreeTraverser();
        TestingHandler testingHandler = new TestingHandler(false);
        jsonPathFinder.addXpath("/$/members/{}/name").handle(testingHandler);
        jsonPathFinder.find(readFile());
    }

    @Test
    public void arrayPredicateLookup() throws Exception {
        JsonTreeTraverser jsonPathFinder = new JsonTreeTraverser();
        TestingHandler testingHandler = new TestingHandler(false);
        jsonPathFinder.addXpath("/$/members/{}/name")
                .handle(testingHandler);
        jsonPathFinder.find(readFile());
    }

    @Test
    public void innerArrayLookup() throws Exception {
        JsonTreeTraverser jsonPathFinder = new JsonTreeTraverser();
        TestingHandler testingHandler = new TestingHandler(true);
        jsonPathFinder.addXpath("/$/members/{}/powers").handle(testingHandler);
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

    private class TestingHandler implements ElementHandler {
        private boolean isArray;

        public TestingHandler(boolean isArray) {
            this.isArray = isArray;
        }

        @Override
        public void startElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
            objectStore.put("something", structureElement.getElementName());
            objectStore.put("one", 123);
        }

        @Override
        public void endElement(StructureElement structureElement, ObjectStore objectStore) throws Exception {
            System.out.println("End: " + structureElement.getElementName());
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public void onText(StructureElement structureElement, ObjectStore objectStore) {
            System.out.println(String.format("%s=%s", structureElement.getElementName(), structureElement.getText()));
        }
    }
}
