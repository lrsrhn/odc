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
package dk.simpletools.odc.core.processing;

public class ValueStore {
  private Object[] values;

  public void initialize(Enum<?> enumValue) {
    if (values == null) {
      Object[] enumValues = enumValue.getClass().getEnumConstants();
      values = new Object[enumValues.length];
    }
  }

  public Object lookupValue(Enum<?> enumValue) {
    initialize(enumValue);
    if (enumValue.ordinal() >= values.length) {
      throw new IndexOutOfBoundsException();
    }
    return values[enumValue.ordinal()];
  }

  public void addValue(Enum<?> enumValue, String value) {
    initialize(enumValue);
    values[enumValue.ordinal()] = value;
  }

  public void resetValue(Enum<?> enumValue) {
    initialize(enumValue);
    values[enumValue.ordinal()] = null;
  }

  public void clear() {
    if (values != null) {
      for (int i = 0; i < values.length; i++) {
        values[i] = null;
      }
    }
  }
}
