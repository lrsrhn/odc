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
package dk.simpletools.odc.core.predicate;

import dk.simpletools.odc.core.processing.StructureElement;

public class AttributePredicate implements Predicate {
  private String attributeName;
  private String attributeValue;

  public AttributePredicate(String attributeName, String attributeValue) {
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
  }

  public AttributePredicate(String attributeName) {
    this.attributeName = attributeName;
    this.attributeValue = null;
  }

  @Override
  public boolean evaluate(StructureElement structureElement) {
    if (attributeName != null) {
      if (attributeValue == null) {
        return structureElement.hasAttribute(attributeName);
      }
      return attributeValue.equals(structureElement.getAttributeValue(attributeName));
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    AttributePredicate that = (AttributePredicate) o;

    if (attributeName != null ? attributeName.equals(that.attributeName) : that.attributeName == null)
      if (attributeValue != null ? attributeValue.equals(that.attributeValue) : that.attributeValue == null)
        return true;
    return false;
  }

  @Override
  public int hashCode() {
    int result = attributeName != null ? attributeName.hashCode() : 0;
    result = 31 * result + (attributeValue != null ? attributeValue.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "@" + attributeName + "='" + attributeValue + "'";
  }
}
