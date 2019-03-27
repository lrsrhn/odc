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
package dk.ott.core.finder;

import dk.ott.core.predicate.Predicate;

import java.util.Set;

class PrettyPrintHelper {

    static void printSearchLocation(SearchLocation searchLocation, StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder) {
        Predicate filter = searchLocation.getFilter();
        printOnEventHandler(previousElementsBuilder, toStringBuilder, filter, searchLocation.getOnStartHandler(), true);
        ElementFinder nextElementFinder = searchLocation.getElementFinder();
        if (nextElementFinder != null) {
            if (!visited.contains(nextElementFinder)) {
                int currentPreviousLength = previousElementsBuilder.length();
                visited.add(nextElementFinder);
                nextElementFinder.buildToString(previousElementsBuilder, visited, toStringBuilder);
                visited.remove(nextElementFinder);
                previousElementsBuilder.setLength(currentPreviousLength);
            } else {
                toStringBuilder
                        .append(previousElementsBuilder)
                        .append(" <=>\n");
            }
        }
        printOnEventHandler(previousElementsBuilder, toStringBuilder, filter, searchLocation.getOnEndHandler(), false);
    }

    private static void printOnEventHandler(StringBuilder previousElementsBuilder, StringBuilder toStringBuilder, Predicate filter, Object handler, boolean isStart) {
        if (handler != null) {
            toStringBuilder.append(previousElementsBuilder);
            if (filter != null) {
                toStringBuilder.append('[').append(filter.toString()).append(']');
            }
            if (isStart) {
                toStringBuilder.append(" [S]=> ")
                        .append(handler.getClass().getName())
                        .append('\n');
            } else {
                toStringBuilder.append(" [E]=> ")
                        .append(handler.getClass().getName())
                        .append('\n');
            }
        }
    }
}
