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

public class ListCollector implements EventHandler {
  private String storeKey;
  private String storeKeyToList;

  public ListCollector(String storeKey, String storeKeyToList) {
    this.storeKey = storeKey;
    this.storeKeyToList = storeKeyToList;
  }

  public void handle(ElementCursor elementCursor, ObjectStore objectStore) {
    List<String> valueList = objectStore.get(storeKey, List.class);
    if (valueList == null) {
      valueList = new ArrayList<String>();
      objectStore.put(storeKey, valueList);
    }
    valueList.add(objectStore.get(storeKeyToList, String.class));
  }

  @Override
  public void onEnd(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
    handle(elementCursor, objectStore);
  }

  @Override
  public void onStart(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
    handle(elementCursor, objectStore);
  }

  @Override
  public void onText(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
    handle(elementCursor, objectStore);
  }
}