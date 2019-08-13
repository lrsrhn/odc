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
package dk.ott.xml;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;

public class XmlStreamBuilderTest {

    private static final String EXPECTED_XML =
                "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<one monkey=\"brians\">\n" +
                "  <stuff>\n" +
                "    <two testing=\"crazy\"/>\n" +
                "  </stuff>\n" +
                "  <stuff2>\n" +
                "    <three>ljsbdfljabldfbdljafdåæø</three>\n" +
                "    <four>¤%¤%¤<cadcad/>%¤åæøEFDAczx\n" +
                "</four>\n" +
                "  </stuff2>\n" +
                "</one>";

    @Test
    public void basicXmlTest() throws XMLStreamException {
        ByteArrayOutputStream asd = new ByteArrayOutputStream();
        PrettyXmlStreamBuilder xmlStreamBuilder2 = new PrettyXmlStreamBuilder(new XmlStreamWriterWrapper(asd));

        xmlStreamBuilder2.addDefaultPI()
                .element("one").attribute("monkey", "brians")
                    .element("stuff")
                        .elementShort("two").attribute("testing", "crazy")
                    .elementEnd()
                    .element("stuff2")
                        .element("three")
                            .value("ljsbdfljabldfbdljafdåæø")
                        .elementEnd()
                        .element("four")
                            .valueNoEscaping("¤%¤%¤<cadcad/>%¤åæøEFDAczx\n")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
                .flush();

        Assert.assertEquals(new String(asd.toByteArray()), EXPECTED_XML);
    }

    @Test
    public void swappingOutputStreamsTest() throws Exception {
        ByteArrayOutputStream mainOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream secondOutputStream = new ByteArrayOutputStream();
        PrettyXmlStreamBuilder xmlStreamBuilder2 = new PrettyXmlStreamBuilder(new XmlStreamWriterWrapper(mainOutputStream));

        xmlStreamBuilder2.addDefaultPI()
                .element("one").attribute("monkey", "brians")
                    .element("stuff")
                        .elementShort("two")
                .swapOutputStream(secondOutputStream);

        xmlStreamBuilder2
                .attribute("testing", "crazy")
                .elementEnd()
                    .element("stuff2")
                        .element("three")
                            .value("ljsbdfljabldfbdljafdåæø")
                        .elementEnd()
                        .element("four")
                            .valueNoEscaping("¤%¤%¤<cadcad/>%¤åæøEFDAczx\n")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
                .flush();

        secondOutputStream.writeTo(mainOutputStream);
        Assert.assertEquals(new String(mainOutputStream.toByteArray()), EXPECTED_XML);
    }
}
