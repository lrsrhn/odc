/**
 * The MIT License
 * Copyright © 2018 Lars Storm
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
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
import com.ximpleware.VTDNav;
import dk.ott.core.finder.ElementFinder;

public final class VtdIndexProcessor extends BaseElementProcessor<VTDNav, XMLElement> {

    public VtdIndexProcessor(ElementFinder nextElementFinder, ObjectStore objectStore) {
        super(nextElementFinder, objectStore);
    }

    public ObjectStore search(VTDNav vtdNav, XMLElement xmlElement) throws Exception {
        AutoPilot ap = new AutoPilot(vtdNav);
        if (vtdNav.toElement(VTDNav.ROOT)) {
            for (int tokenIndex = vtdNav.getCurrentIndex(); tokenIndex < vtdNav.getTokenCount(); tokenIndex++) {
                System.out.println("token count => "+tokenIndex + ", Type: " + typeToText(vtdNav.getTokenType(tokenIndex)));
                if (vtdNav.getTokenType(tokenIndex) == VTDNav.TOKEN_STARTING_TAG) {
                    System.out.println("Element length: " + vtdNav.getTokenLength(tokenIndex));
                    System.out.println("Element length: " + vtdNav.toString(tokenIndex));
                }
            }
        }
        return objectStore;
    }

    private static String typeToText(int type) {
        switch (type) {
            case VTDNav.TOKEN_STARTING_TAG:
                return "Start element";
            case VTDNav.TOKEN_ENDING_TAG:
                return "End element";
            case VTDNav.TOKEN_CHARACTER_DATA:
                return "Characters";
            default:
                return type + "";
        }
    }
}
