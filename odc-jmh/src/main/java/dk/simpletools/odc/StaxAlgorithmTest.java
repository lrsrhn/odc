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
package dk.simpletools.odc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

@Fork(value = 1)
@Warmup(iterations = 1)
@Measurement(iterations = 5, time = 5)
@BenchmarkMode(Mode.SampleTime)
public class StaxAlgorithmTest {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String xmlContent;
        XMLInputFactory xmlInputFactory;
        StringBuilder builder = new StringBuilder(2000000);

        public BenchmarkState() {
            try {
                xmlContent = readFile();
                xmlInputFactory = XMLInputFactory.newFactory();
                xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

//    @Benchmark
    public void normalAlgorithm(BigXmlStaxBenchmark.BenchmarkState benchmarkState, final Blackhole blackhole) throws Exception {
        XMLStreamReader streamReader = benchmarkState.xmlInputFactory.createXMLStreamReader(new StringReader(benchmarkState.xmlContent));
        while (streamReader.hasNext()) {
            switch (streamReader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    if ("registered".equals(streamReader.getLocalName()) || "tags".equals(streamReader.getLocalName())) {
                        benchmarkState.builder.append(streamReader.getLocalName()).append(": ").append(streamReader.getElementText());
                    }
                    break;
                default:
                    break;
            }
        }
//        System.out.println("Length: " + benchmarkState.builder.length());
        benchmarkState.builder.setLength(0);
        streamReader.close();
    }

//    @Benchmark
    public void alternteAlgorithm(BigXmlStaxBenchmark.BenchmarkState benchmarkState, final Blackhole blackhole) throws Exception {
        XMLStreamReader streamReader = benchmarkState.xmlInputFactory.createXMLStreamReader(new StringReader(benchmarkState.xmlContent));
        if (streamReader.hasNext()) {
            for (int eventType = streamReader.next(); streamReader.hasNext(); eventType = streamReader.next()) {
                switch (eventType) {
                    case XMLStreamReader.START_ELEMENT:
                        if ("registered".equals(streamReader.getLocalName()) || "tags".equals(streamReader.getLocalName())) {
                            benchmarkState.builder.append(streamReader.getLocalName()).append(": ").append(streamReader.getElementText());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
//        System.out.println("Length: " + benchmarkState.builder.length());
        benchmarkState.builder.setLength(0);
        streamReader.close();
    }

    private static String readFile() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                StaxAlgorithmTest.class.getClassLoader().getResourceAsStream("bigjson.xml")));
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
