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

public class TextPredicate implements Predicate {
  private String expectedText;

  public TextPredicate(String expectedText) {
    this.expectedText = expectedText;
  }

  @Override
  public boolean evaluate(ElementCursor elementCursor, ObjectStore objectStore) {
    return expectedText.equals(elementCursor.getText());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    TextPredicate that = (TextPredicate) o;

    return expectedText != null ? expectedText.equals(that.expectedText) : that.expectedText == null;
  }

  @Override
  public int hashCode() {
    return expectedText != null ? expectedText.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "text()='" + expectedText + "'";
  }
}