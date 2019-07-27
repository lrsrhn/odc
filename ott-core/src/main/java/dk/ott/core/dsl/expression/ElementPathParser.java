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

import java.util.ArrayList;
import java.util.List;

public class ElementPathParser {

    public static List<Element> parseElementPath(String elementPath) {
        if (elementPath == null || elementPath.length() < 2) {
            throw new IllegalStateException("Too short element path provided");
        }

        boolean isRelative;
        int startIndex = 0;
        List<Element> elements = new ArrayList<Element>();
        while (startIndex < elementPath.length()) {
            if (elementPath.startsWith("//", startIndex)) {
                startIndex+=2;
                isRelative = true;
            } else if (elementPath.startsWith("/")) {
                startIndex++;
                isRelative = false;
            } else {
                throw new IllegalStateException("The provided path must start with a / or // at position: " + startIndex);
            }

            int endIndex = findEndIndex(elementPath, startIndex);
            elements.add(new Element(extractElement(elementPath, startIndex, endIndex), isRelative));
            startIndex = endIndex;
        }
        return elements;
    }

    private static String extractElement(String elementPath, int startIndex, int endIndex) {
        String element = elementPath.substring(startIndex, endIndex);
        if (element.isEmpty()) {
            throw new IllegalStateException("An element in the path cannot be empty");
        }
        return element;
    }

    private static int findEndIndex(String elementPath, int startIndex) {
        int endIndex = elementPath.indexOf('/', startIndex);
        return endIndex == -1 ? elementPath.length() : endIndex;
    }
}
