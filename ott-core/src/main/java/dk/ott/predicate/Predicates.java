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

public class Predicates {
  public static Predicate or(Predicate... predicates) {
    return new OrPredicate(predicates);
  }

  public static Predicate and(Predicate... predicates) {
    return new AndPredicate(predicates);
  }

  public static Predicate attribute(String attributeName, String attributeValue) {
    return new AttributePredicate(attributeName, attributeValue);
  }

  public static Predicate storedValue(String storeKey, String value) {
    return new ValueStorePredicate(storeKey, value);
  }

  public static Predicate text(String text) {
    return new TextPredicate(text);
  }

  public static Predicate namespace(String namespace) {
    return new NamespacePredicate(namespace);
  }

  public static Predicate noNamespace() {
    return new NamespacePredicate(null);
  }

  public static Predicate namespaceEmpty() {
    return new NamespacePredicate(null);
  }

  public static Predicate alwaysTrue() {
    return TruePredicate.INSTANCE;
  }

}
