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

import static dk.ott.core.processing.TextTrimmer.trimToNull;

public class VtdElement extends BaseStructureElement {
  private VTDNav vtdNav;
  private AutoPilot autoPilot;
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

  void setTokenIndex(int tokenIndex) {
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
            return trimToNull(vtdNav.toNormalizedString(index));
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
        elementTextCache = trimToNull(vtdNav.toNormalizedString2(tokenIndex));
      }
      return elementTextCache;
    } catch (NavException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public String getElementNS() {
    try {
      if (elementNamespaceCache == null && vtdNav.getTokenType(tokenIndex) == VTDNav.TOKEN_STARTING_TAG) {
        vtdNav.recoverNode(tokenIndex);
        elementNamespaceCache = trimToNull(autoPilot.evalXPathToString());
      }
      return elementNamespaceCache;
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  void setElementName(String elementName) {
    this.elementNameCache = elementName;
  }
}
