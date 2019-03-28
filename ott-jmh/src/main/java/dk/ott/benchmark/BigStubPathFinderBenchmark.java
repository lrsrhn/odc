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

import dk.ott.core.stub.InputReader;
import dk.ott.core.stub.StubTreeTraverser;
import dk.ott.core.stub.XmlToInputReader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

//jvmArgsAppend={"-XX:MaxInlineSize=0", "-Xverify:none"}

// jvmArgsAppend = {"-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining"}
@Fork(value = 1, jvmArgsAppend={"-Xverify:none"})
@Warmup(iterations = 1)
//@Measurement(iterations = 100, time = 30)
@Measurement(iterations = 5, time = 10)
@BenchmarkMode(Mode.SampleTime)
public class BigStubPathFinderBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String xmlContent;
        InputReader inputReader;
        StubTreeTraverser stubPathFinder;
        private StringBuilder builder = new StringBuilder(2000000);

        public BenchmarkState() {
            try {
                String xmlContent = readFile();
                inputReader = new XmlToInputReader().processXml(new StringReader(xmlContent));
                stubPathFinder = new StubTreeTraverser();
                ToStringBuilderHandler testHandler = new ToStringBuilderHandler(builder);
                stubPathFinder.addXpath("/root/row/registered").onText(testHandler);
//                xppPathFinder.addXpath("/root/row/greeting").onText(testHandler);
//                xppPathFinder.addXpath("/root/row/latitude").onText(testHandler);
                stubPathFinder.addXpath("/root/row/tags").onText(testHandler);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }


//    @Benchmark
    // Broken!
    public void testBigXml(BenchmarkState benchmarkState, final Blackhole blackhole) {
        benchmarkState.inputReader.reset();
        blackhole.consume(benchmarkState.stubPathFinder.find(benchmarkState.inputReader));
        if (benchmarkState.builder.length() != 1191873) {
            throw new IllegalStateException(String.format("Length was %d but should be 1191873", benchmarkState.builder.length()));
        }
        benchmarkState.builder.setLength(0);
    }

    private static String readFile() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                BigStubPathFinderBenchmark.class.getClassLoader().getResourceAsStream("bigjson.xml")));
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
