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
import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

public class BigXmlStaxBenchmark {
    private static long counter = 0;
        private static String xmlContent = readFile();
        private static XMLInputFactory xmlInputFactory;
        StringBuilder builder = new StringBuilder(2000000);

        static {
            try {
                xmlInputFactory = XMLInputFactory.newFactory();
                xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

    @Test
    public void bigXmlStaxBenchmark() throws Exception {
        XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(new StringReader(xmlContent));
        counter = 0;
        while (streamReader.hasNext()) {
            findRoot(streamReader, builder);
        }
        System.out.println("Length: " + builder.length());
        builder.setLength(0);
        System.out.println("Inner checks: " + counter);
        streamReader.close();
    }

    private boolean findRoot(XMLStreamReader streamReader, StringBuilder builder) throws Exception {
        int depthCounter = 0;
        while (streamReader.hasNext()) {
            switch (streamReader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    if ("root".equals(streamReader.getLocalName())) {
                        findRow(streamReader, builder);
                    }
                    depthCounter++;
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (depthCounter == 0) {
                        return false;
                    }
                    depthCounter--;
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private boolean findRow(XMLStreamReader streamReader, StringBuilder builder) throws Exception {
        int depthCounter = 0;
        while (streamReader.hasNext()) {
            switch (streamReader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    if ("row".equals(streamReader.getLocalName())) {
                        findTwins(streamReader, builder);
                    }
                    depthCounter++;
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (depthCounter == 0) {
                        return false;
                    }
                    depthCounter--;
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private boolean findTwins(XMLStreamReader streamReader, StringBuilder builder) throws Exception {
        int depthCounter = 0;
        while (streamReader.hasNext()) {
            switch (streamReader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    depthCounter++;
                    if (depthCounter == 1) {
                        counter++;
//                    if ("registered".equals(streamReader.getLocalName()) || "greeting".equals(streamReader.getLocalName()) || "latitude".equals(streamReader.getLocalName()) || "tags".equals(streamReader.getLocalName())) {
                        if ("registered".equals(streamReader.getLocalName()) || "tags".equals(streamReader.getLocalName())) {
                            builder.append(streamReader.getLocalName()).append(": ").append(streamReader.getElementText());
                            depthCounter--;
                        }
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (depthCounter == 0) {
                        return false;
                    }
                    depthCounter--;
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private static String readFile() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    BigXmlStaxBenchmark.class.getClassLoader().getResourceAsStream("bigjson.xml")));
            StringBuilder builder = new StringBuilder();
            while (reader.ready()) {
                builder.append(reader.readLine());
                builder.append("\n");
            }
            reader.close();
            builder.setLength(builder.length() - 1);
            return builder.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
