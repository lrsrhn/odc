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
package dk.ott.eventhandling;

import dk.ott.event.EventHandler;
import dk.ott.processing.ElementCursor;
import dk.ott.processing.ObjectStore;

import java.util.ArrayList;
import java.util.List;

public class TextValueCollector implements EventHandler {

  private String storeKey;

  public TextValueCollector(String storeKey) {
    this.storeKey = storeKey;
  }

  public void handle(ElementCursor elementCursor, ObjectStore objectStore) {
    // TODO: 12/10/19 Add support for append strategies
    List<String> values = objectStore.get(storeKey, List.class);
    if (values != null) {
      values.add(elementCursor.getText());
      return;
    }
    String value = objectStore.get(storeKey, String.class);
    if (value == null) {
      objectStore.put(storeKey,  elementCursor.getText());
      return;
    }
    values = new ArrayList<String>(3);
    values.add(value);
    values.add(elementCursor.getText());
    objectStore.put(storeKey,  values);
  }

  @Override
  public void onEnd(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
    // Nothing
  }

  @Override
  public void onStart(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
    // Nothing
  }

  @Override
  public void onText(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
    handle(elementCursor, objectStore);
  }
}