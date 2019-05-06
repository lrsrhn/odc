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
package dk.ott.core.dsl;

import dk.ott.core.event.OnStartHandler;
import dk.ott.core.event.OnTextHandler;
import dk.ott.core.predicate.Predicates;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.ObservableTree;
import dk.ott.core.processing.StructureElement;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class Example {
    private static String xmlFileContent = readFile();

    protected ObservableTree observableTree;

    @Before
    public abstract void setObservablePathFinder();


    // @formatter:off
    @Test
    public void example() {
        observableTree.treeBuilder()
                .element("bookstore")
                    .element("book")
                        .predicate(Predicates.namespace(""))
                            .element("author")
                                .element("first-name")
                                    .onText().to(new MyOnStartHandler("Author", false))
                                .end()
                                .element("publication")
                                    .onStart().to(new MyOnStartHandler("Publication", true))
                                .end()
                            .end()
                            .relativeElement("p")
                                .onStart().to(new MyOnStartHandler("Paragraph", true))
                            .end()
                        .end()
                        .predicate(Predicates.namespace("uri:mynamespace"))
                            .element("author")
                                .onText().to(new MyOnStartHandler("My author", false))
                            .end()
                            .element("title")
                                .onText().to(new MyOnStartHandler("My title", false))
                            .end()
                        .end()
                    .end()
                    .element("magazine")
                        .element("price")
                            .onText().to(new MyOnStartHandler("Magazine price", false))
                        .end()
                    .end()
                .end()
            .build();
        System.out.println(observableTree.toString());
        observableTree.find(xmlFileContent);
    }

    // @formatter:on
    private static String readFile() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    Example.class.getClassLoader().getResourceAsStream("example.xml")));
            StringBuilder builder = new StringBuilder();
            while (reader.ready()) {
                builder.append(reader.readLine());
                builder.append("\n");
            }
            reader.close();
            builder.setLength(builder.length() - 1);
            return builder.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static class MyOnStartHandler implements OnStartHandler, OnTextHandler {
        private String prefix;
        private boolean isRaw;

        MyOnStartHandler(String prefix, boolean isRaw) {
            this.prefix = prefix;
            this.isRaw = isRaw;
        }

        @Override
        public void onStart(StructureElement structureElement, ObjectStore objectStore) throws Exception {
            if (isRaw) {
                System.out.println(prefix + ": " + structureElement.getRawElementValue());
            }
        }

        @Override
        public void onText(StructureElement structureElement, ObjectStore objectStore) {
            System.out.println(prefix + ": " + structureElement.getText());
        }
    }
}
