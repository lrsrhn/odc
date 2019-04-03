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
package dk.ott.core.processing;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;

public class VtdElement implements InternalStructureElement {
  private VTDNav vtdNav;
  private AutoPilot autoPilot;
  private String elementTextCache;
  private String elementNameCache;
  private String elementNamespaceCache;
  private int tokenIndex;

  public VtdElement(VTDNav vtdNav, AutoPilot autoPilot) {
    try {
      this.vtdNav = vtdNav;
      this.autoPilot = autoPilot;
      autoPilot.selectXPath("namespace-uri()");
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public void setTokenIndex(int tokenIndex) {
    this.tokenIndex = tokenIndex;
  }

  public String getElementName() {
    try {
      if (elementNameCache == null && vtdNav.getTokenType(tokenIndex) == VTDNav.TOKEN_STARTING_TAG) {
        String prefix = vtdNav.getPrefixString(tokenIndex);
        elementNameCache = vtdNav.toNormalizedString(tokenIndex);
        // Remove prefix
        if (prefix != null) {
          elementNameCache = elementNameCache.substring(prefix.length() + 1); // Prefix + ':' length
        }
      }
      return elementNameCache;
    } catch (NavException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public String getAttributeValue(String attributeName) {
    try {
      if (vtdNav.getTokenType(tokenIndex) == VTDNav.TOKEN_STARTING_TAG) {
        for (int index = tokenIndex + 1; index < vtdNav.getTokenCount() && vtdNav.getTokenType(index) == VTDNav.TOKEN_ATTR_NAME; index++) {
          if (attributeName.equals(vtdNav.toNormalizedString(index)) && vtdNav.getTokenType(++index) == VTDNav.TOKEN_ATTR_VAL) {
            return vtdNav.toNormalizedString(index);
          }
        }
      }
      return null;
    } catch (NavException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public boolean hasAttribute(String attributeName) {
    try {
      if (vtdNav.getTokenType(tokenIndex) == VTDNav.TOKEN_STARTING_TAG) {
        for (int index = tokenIndex + 1; index < vtdNav.getTokenCount() && vtdNav.getTokenType(index) == VTDNav.TOKEN_ATTR_NAME; index++) {
          if (attributeName.equals(vtdNav.toNormalizedString(index)) && vtdNav.getTokenType(++index) == VTDNav.TOKEN_ATTR_VAL) {
            return true;
          }
        }
      }
      return false;
    } catch (NavException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public String getText() {
    try {
      if (elementTextCache == null && vtdNav.getTokenType(tokenIndex) == VTDNav.TOKEN_CHARACTER_DATA) {
        elementTextCache = vtdNav.toNormalizedString(tokenIndex);
      }
      return elementTextCache;
    } catch (NavException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * NB: Only use this after all attributes have been read - Calling this will
   * move the XML parser forward
   *
   */
  @Override
  public String getRawElementValue() {
//    try {
//      if (elementTextCache == null) {
//        if (xmlStreamReader.getEventType() != XMLStreamConstants.START_ELEMENT) {
//          throw new RuntimeException("Cannot retrieve raw value from other event then Start_element");
//        }
//        xmlRawTextReader.setStartIndex(xmlStreamReader.getLocation().getCharacterOffset());
//        xmlStreamReader.skipElement();
//        elementTextCache = xmlRawTextReader.readRawText(xmlStreamReader.getLocation().getCharacterOffset());
//      }
//      return elementTextCache;
//    } catch (XMLStreamException xse) {
//      throw new RuntimeException(xse);
//    }
    return "";
  }

  @Override
  public void skipElement() {
//    try {
//      xmlStreamReader.skipElement();
//    } catch (XMLStreamException xse) {
//      throw new RuntimeException(xse);
//    }
  }

  public void clearCache() {
    elementNamespaceCache = null;
    elementNameCache = null;
    elementTextCache = null;
  }

  public String getElementNS() {
    try {
      if (elementNamespaceCache == null && vtdNav.getTokenType(tokenIndex) == VTDNav.TOKEN_STARTING_TAG) {
        vtdNav.recoverNode(tokenIndex);
        elementNamespaceCache = autoPilot.evalXPathToString();
      }
      return elementNamespaceCache;
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public void setElementName(String elementName) {
    this.elementNameCache = elementName;
  }
}
