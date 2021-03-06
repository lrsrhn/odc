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
package dk.ott.predicate;

import dk.ott.processing.ObjectStore;
import dk.ott.processing.ElementCursor;

public class NamespacePredicate implements Predicate {
    private String expectedNamespace;

    public NamespacePredicate(String expectedNamespace) {
        if (expectedNamespace == null) {
            return;
        }
        this.expectedNamespace = expectedNamespace.intern();
    }

    @Override
    public boolean evaluate(ElementCursor elementCursor, ObjectStore objectStore) {
        if (expectedNamespace == null) {
            return elementCursor.getElementNS() == null;
        }
        return expectedNamespace.equals(elementCursor.getElementNS());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamespacePredicate that = (NamespacePredicate) o;

        return expectedNamespace != null ? expectedNamespace.equals(that.expectedNamespace) : that.expectedNamespace == null;
    }

    @Override
    public int hashCode() {
        return expectedNamespace != null ? expectedNamespace.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "namespace-uri()='" + expectedNamespace + "'";
    }
}
