package dk.ott.processing.structures;

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

import javax.json.stream.JsonParser.Event;
import java.util.Arrays;

public final class JsonEventStack {
  private Event[] array;
  private int head;

  public JsonEventStack(int size) {
    this.array = new Event[size];
    this.head = -1;
  }

  public Event pop() {
    if (head == -1) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return array[head--];
  }

  public Event peek() {
    if (head > -1) {
    return array[head];
    } else if (head == -1) {
      return null;
    }
    throw new ArrayIndexOutOfBoundsException();
  }

  public void push(Event value) {
    if (head == array.length - 1) {
      array = Arrays.copyOf(array, array.length * 2);
    }
    array[++head] = value;
  }

  public int size() {
    return head + 1;
  }
}
