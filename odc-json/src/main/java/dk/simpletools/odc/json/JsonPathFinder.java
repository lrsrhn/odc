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
package dk.simpletools.odc.json;

import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.ObservablePathFinder;
import org.omg.CORBA.Object;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import java.io.Reader;
import java.util.HashMap;

public class JsonPathFinder extends ObservablePathFinder {
  private static final JsonParserFactory DEFAULT_JSON_PARSER_FACTORY = Json.createParserFactory(new HashMap<String, Object>());
  private JsonParserFactory jsonParserFactory;

  public JsonPathFinder(JsonParserFactory jsonParserFactory) {
    this.jsonParserFactory = jsonParserFactory;
  }

  public JsonPathFinder() {
    this(DEFAULT_JSON_PARSER_FACTORY);
  }

  public ObjectStore find(Reader reader) {
    JsonParser jsonParser = null;
    try {
      jsonParser = jsonParserFactory.createParser(reader);
      JsonObject jsonObject = new JsonObject(jsonParser);
      ObjectProcessor objectProcessor = new ObjectProcessor(rootElementFinder.getElementFinder(), jsonObject);
      return objectProcessor.search(jsonParser, jsonObject);
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    } finally {
      if (jsonParser != null) {
        try {
          jsonParser.close();
        } catch (Exception ex) {
          // Ignore
        }
      }
    }
  }
}