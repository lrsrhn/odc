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
package dk.ott.processing;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

import java.io.Reader;
import java.nio.charset.Charset;

public class VtdXmlObservableTree extends ObservableTree {

  @Override
  public ObjectStore find(Reader reader, ObjectStore objectStore) {
    return find(toByteArray(reader), objectStore);
  }

  public ObjectStore find(byte[] documentBytes) {
    return find(documentBytes, new ObjectStore());
  }

  public ObjectStore find(byte[] documentBytes, ObjectStore objectStore) {
    try {
      VTDGen vtdGen = new VTDGen();
      vtdGen.setDoc(documentBytes);
      vtdGen.parse(true); // Parse with namespace support enabled
      VTDNav vtdNav = vtdGen.getNav();
      AutoPilot autoPilot = new AutoPilot(vtdNav);
      VtdElementCursor xmlElement = new VtdElementCursor(vtdNav, autoPilot);
      VtdIndexProcessor vtdIndexProcessor = new VtdIndexProcessor(rootEdgeReference.getNode(), objectStore);
      return vtdIndexProcessor.search(vtdNav, xmlElement);
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }
  }

  public static byte[] toByteArray(Reader reader) {
    try {
      char[] charArray = new char[8 * 1024];
      StringBuilder builder = new StringBuilder();
      int numCharsRead;
      while ((numCharsRead = reader.read(charArray, 0, charArray.length)) != -1) {
        builder.append(charArray, 0, numCharsRead);
      }
      return builder.toString().getBytes(Charset.forName("UTF-8"));
    } catch (Exception ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Exception ex) {
          // Ignore
        }
      }
    }
  }
}