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
package dk.ott.core.predicate;

import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.ElementCursor;

import java.util.Arrays;

public class AndPredicate implements Predicate {
  private Predicate[] predicates;

  public AndPredicate(Predicate[] predicates) {
    this.predicates = predicates;
  }

  @Override
  public boolean evaluate(ElementCursor elementCursor, ObjectStore objectStore) {
    for (Predicate predicate : predicates) {
      if (!predicate.evaluate(elementCursor, objectStore)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    AndPredicate that = (AndPredicate) o;

    return Arrays.equals(predicates, that.predicates);
  }

  @Override
  public int hashCode() {
    return 31 * Arrays.hashCode(predicates) + this.getClass().hashCode();
  }

  @Override
  public String toString() {
    if (predicates == null || predicates.length == 0) {
      return "";
    }
    String AND = " and ";
    StringBuilder builder = new StringBuilder(predicates.length * 50);
    for (Predicate predicate : predicates) {
      builder.append(predicate).append(AND);
    }
    builder.setLength(builder.length() - AND.length());
    return builder.toString();
  }
}
