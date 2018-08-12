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
package dk.simpletools.odc.core.standardhandlers;

import dk.simpletools.odc.core.picker.ValuePicker;
import dk.simpletools.odc.core.processing.EndElement;
import dk.simpletools.odc.core.processing.StructureElement;
import dk.simpletools.odc.core.processing.ValueStore;
import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.finder.ElementHandler;

public class ValuePickerToValueStore implements ElementHandler {
  private ValuePicker[] pickers;

  public ValuePickerToValueStore(ValuePicker... pickers) {
    if (pickers == null || pickers.length == 0) {
      throw new IllegalArgumentException("ValuePickers must be set");
    }
    this.pickers = pickers;
  }

  @Override
  public void startElement(StructureElement structureElement) throws Exception {
    for (ValuePicker picker : pickers) {
      structureElement.getValueStore().addValue(picker.getValueStoreIndex(), picker.pick(structureElement));
    }
  }

  @Override
  public void endElement(EndElement endElement, ValueStore valueStore, ObjectStore objectStore) throws Exception {

  }

  @Override
  public void clear() {
  }
}