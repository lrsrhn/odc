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
package dk.ott.core;

import dk.ott.event.OnTextHandler;
import dk.ott.predicate.Predicate;

public class TextLocation {
    private Predicate textFilter;
    private boolean isRaw;
    private OnTextHandler onTextHandler;

    public Predicate getTextFilter() {
        return textFilter;
    }

    public TextLocation setTextFilter(Predicate textFilter) {
        this.textFilter = textFilter;
        return this;
    }

    public OnTextHandler getOnTextHandler() {
        return onTextHandler;
    }

    public TextLocation setOnTextHandler(OnTextHandler onTextHandler) {
        this.onTextHandler = onTextHandler;
        return this;
    }

    public boolean isRaw() {
        return isRaw;
    }

    public TextLocation setRaw(boolean raw) {
        isRaw = raw;
        return this;
    }
}
