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
package dk.ott.core.dsl.expression;

import dk.ott.core.dsl.adders.TreeElementAdder;
import dk.ott.core.dsl.adders.TreePathAdder;
import dk.ott.core.dsl.adders.TreeRootElementAdder;

import java.util.ArrayList;
import java.util.List;

public class XpathParser {

    public static List<TreePathAdder> parseXpath(String xpath, boolean firstIsRoot) {
        boolean isRelative = false;
        int startIndex;
        if (xpath.startsWith("//")) {
            startIndex = 2;
            isRelative = true;
        } else if (xpath.startsWith("/")) {
            startIndex = 1;
        } else {
            throw new RuntimeException("The provided path must start with a / or //");
        }

        List<TreePathAdder> treeElementAdders = new ArrayList<TreePathAdder>();
        List<String> elements = new ArrayList<String>();
        while (startIndex < xpath.length()) {
            int endIndex = findEndIndex(xpath, startIndex);
            String element = extractElement(xpath, startIndex, endIndex);
            elements.add(element);
            endIndex++;
            if (endIndex < xpath.length() && xpath.charAt(endIndex) == '/') {
                endIndex++;
                if (!isRelative) {
                    addElementsAsTreeElementAdders(firstIsRoot, false, treeElementAdders, elements);
                    elements.clear();
                }
                isRelative = true;
            } else {
                if (isRelative) {
                    addElementsAsTreeElementAdders(firstIsRoot, true, treeElementAdders, elements);
                    elements.clear();
                }
                isRelative = false;
            }
            startIndex = endIndex;
        }
        if (!elements.isEmpty()) {
            addElementsAsTreeElementAdders(firstIsRoot, isRelative, treeElementAdders, elements);
        }
        return treeElementAdders;
    }

    private static String extractElement(String xpath, int startIndex, int endIndex) {
        String element = xpath.substring(startIndex, endIndex);
        if (element.isEmpty()) {
            throw new RuntimeException("An element in the path cannot be empty");
        }
        return element;
    }

    private static int findEndIndex(String xpath, int startIndex) {
        int endIndex = xpath.indexOf("/", startIndex);
        endIndex = endIndex == -1 ? xpath.length() : endIndex;
        return endIndex;
    }

    private static void addElementsAsTreeElementAdders(boolean firstIsRoot, boolean isRelative, List<TreePathAdder> treeElementAdders, List<String> elements) {
        if (treeElementAdders.isEmpty() && firstIsRoot) {
            treeElementAdders.add(new TreeRootElementAdder(elements.get(0), isRelative));
            elements = elements.subList(1, elements.size());
        }
        if (!elements.isEmpty()) {
            treeElementAdders.add(new TreeElementAdder(elements.toArray(new String[elements.size()]), isRelative));
        }
    }
}
