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
package dk.ott.benchmark;

import com.ctc.wstx.stax.WstxInputFactory;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

@Fork(value = 1)
@Warmup(iterations = 1)
@Measurement(iterations = 5, time = 10)
@BenchmarkMode(Mode.SampleTime)
public class BigStaxBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String xmlContent;
        WstxInputFactory xmlInputFactory;
        StringBuilder builder = new StringBuilder(2000000);

        public BenchmarkState() {
            try {
                xmlContent = readFile();
                xmlInputFactory = new WstxInputFactory();
                xmlInputFactory.configureForConvenience();
                xmlInputFactory.setProperty(WstxInputFactory.P_INTERN_NAMES, false);
                xmlInputFactory.setProperty(WstxInputFactory.P_INTERN_NS_URIS, false);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Benchmark
    public void bigXmlStaxBenchmark(BenchmarkState benchmarkState, final Blackhole blackhole) throws Exception {
        XMLStreamReader streamReader = benchmarkState.xmlInputFactory.createXMLStreamReader(new StringReader(benchmarkState.xmlContent));
        while (streamReader.hasNext()) {
            findRoot(streamReader, benchmarkState.builder);
        }
        if (benchmarkState.builder.length() != 1191873) {
            throw new IllegalStateException(String.format("Length was %d but should be 1191873", benchmarkState.builder.length()));
        }
        benchmarkState.builder.setLength(0);
        streamReader.close();
    }

    private boolean findRoot(XMLStreamReader streamReader, StringBuilder builder) throws Exception {
        int depthCounter = 0;
        while (streamReader.hasNext()) {
            switch (streamReader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    depthCounter++;
                    if (depthCounter == 1 && "root".equals(streamReader.getLocalName())) {
                        findRow(streamReader, builder);
                        depthCounter--;
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

    private boolean findRow(XMLStreamReader streamReader, StringBuilder builder) throws Exception {
        int depthCounter = 0;
        while (streamReader.hasNext()) {
            switch (streamReader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    depthCounter++;
                    if (depthCounter == 1 && "row".equals(streamReader.getLocalName())) {
                        findTwins(streamReader, builder);
                        depthCounter--;
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

    private boolean findTwins(XMLStreamReader streamReader, StringBuilder builder) throws Exception {
        int depthCounter = 0;
        boolean found = false;
        while (streamReader.hasNext()) {
            switch (streamReader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    depthCounter++;
//                    if ("registered".equals(streamReader.getLocalName()) || "greeting".equals(streamReader.getLocalName()) || "latitude".equals(streamReader.getLocalName()) || "tags".equals(streamReader.getLocalName())) {
                    if (depthCounter == 1 && ("registered".equals(streamReader.getLocalName()) || "tags".equals(streamReader.getLocalName()))) {
                        builder.append(streamReader.getLocalName()).append(": ");
                        found = true;
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (found) {
                        builder.append(streamReader.getText());
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    found = false;
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

    private static String readFile() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                BigStaxBenchmark.class.getClassLoader().getResourceAsStream("bigjson.xml")));
        StringBuilder builder = new StringBuilder();
        while (reader.ready()) {
            builder.append(reader.readLine());
            builder.append("\n");
        }
        reader.close();
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}
