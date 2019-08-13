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
package dk.ott.util;

import dk.ott.core.Edge;
import dk.ott.core.Node;
import dk.ott.predicate.Predicate;

import java.util.Set;

public class TreePrettyPrintHelper {

    public static void printSearchLocation(Edge edge, StringBuilder previousElementsBuilder, Set<Node> visited, StringBuilder toStringBuilder) {
        Predicate filter = edge.getFilter();
        printOnEventHandler(previousElementsBuilder, toStringBuilder, filter, edge.getOnStartHandler(), " [S]=> ");
        if (edge.getTextLocation() != null) {
            printOnEventHandler(previousElementsBuilder, toStringBuilder, edge.getTextLocation().getTextFilter(), edge.getTextLocation().getOnTextHandler(), " [T]=> ");
        }
        Node nextNode = edge.getChildNode();
        if (nextNode != null) {
            if (!visited.contains(nextNode)) {
                int currentPreviousLength = previousElementsBuilder.length();
                visited.add(nextNode);
                nextNode.buildToString(previousElementsBuilder, visited, toStringBuilder);
                visited.remove(nextNode);
                previousElementsBuilder.setLength(currentPreviousLength);
            } else {
                toStringBuilder
                        .append(previousElementsBuilder)
                        .append(" <=>\n");
            }
        }
        printOnEventHandler(previousElementsBuilder, toStringBuilder, filter, edge.getOnEndHandler(), " [E]=> ");
    }

    private static void printOnEventHandler(StringBuilder previousElementsBuilder, StringBuilder toStringBuilder, Predicate filter, Object handler, String handlerTypeText) {
        if (handler != null) {
            toStringBuilder.append(previousElementsBuilder);
            if (filter != null) {
                toStringBuilder.append('[').append(filter.toString()).append(']');
            }
            toStringBuilder.append(handlerTypeText)
                    .append(handler.getClass().getName())
                    .append('\n');
        }
    }
}
