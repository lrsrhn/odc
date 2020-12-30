///**
// * The MIT License
// * Copyright © 2018 Lars Storm
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package dk.ott.benchmark;
//
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.infra.Blackhole;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.StringReader;
//
//@Fork(value = 1)
//@Warmup(iterations = 1)
//@Measurement(iterations = 5, time = 10)
//@BenchmarkMode(Mode.SampleTime)
//public class BigDomBenchmark {
//
//    @State(Scope.Benchmark)
//    public static class BenchmarkState {
//        String xmlContent;
//        private DocumentBuilderFactory documentBuilderFactory;
//        StringBuilder builder = new StringBuilder(2000000);
//
//        public BenchmarkState() {
//            try {
//                xmlContent = readFile();
//                documentBuilderFactory = DocumentBuilderFactory.newInstance();
//              documentBuilderFactory.setNamespaceAware(true);
//              documentBuilderFactory.setCoalescing(true);
//              documentBuilderFactory.setIgnoringElementContentWhitespace(true);
//              documentBuilderFactory.setIgnoringComments(true);
//            } catch (Exception ex) {
//                throw new RuntimeException(ex);
//            }
//        }
//    }
//
//    @Benchmark
//    public void bigDomBenchmark(BenchmarkState benchmarkState, final Blackhole blackhole) throws Exception {
//        Document document = benchmarkState.documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(benchmarkState.xmlContent)));
//
//        Element root = document.getDocumentElement();
//        NodeList nodeList = root.getElementsByTagName("row");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Element row = (Element) nodeList.item(i);
//            NodeList registered = row.getElementsByTagName("registered");
//            for (int j = 0; j < registered.getLength(); j++) {
//                Node register = registered.item(j);
//                benchmarkState.builder.append(register.getLocalName()).append(": ").append(register.getTextContent());
//            }
//            NodeList tags = row.getElementsByTagName("tags");
//            for (int j = 0; j < tags.getLength(); j++) {
//                Node tag = tags.item(j);
//                benchmarkState.builder.append(tag.getLocalName()).append(": ").append(tag.getTextContent());
//            }
//        }
//
//        if (benchmarkState.builder.length() != 1191873) {
//            throw new IllegalStateException(String.format("Length was %d but should be 1191873", benchmarkState.builder.length()));
//        }
//        benchmarkState.builder.setLength(0);
//    }
//
//    private static String readFile() throws Exception {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                BigDomBenchmark.class.getClassLoader().getResourceAsStream("bigjson.xml")));
//        StringBuilder builder = new StringBuilder();
//        while (reader.ready()) {
//            builder.append(reader.readLine());
//            builder.append("\n");
//        }
//        reader.close();
//        builder.setLength(builder.length() - 1);
//        return builder.toString();
//    }
//}
