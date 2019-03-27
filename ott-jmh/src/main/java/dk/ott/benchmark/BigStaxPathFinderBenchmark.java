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

import dk.ott.xml.StaxPathFinder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//,jvmArgsAppend={"-XX:MaxInlineSize=0", "-Xverify:none"}
// jvmArgsAppend = {"-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining"}
@Fork(value = 1)
@Warmup(iterations = 1)
//@Measurement(iterations = 100, time = 30)
@Measurement(iterations = 5, time = 10)
@BenchmarkMode(Mode.SampleTime)
public class BigStaxPathFinderBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String xmlContent;
        StaxPathFinder staxPathFinder;
        private StringBuilder builder = new StringBuilder(2000000);

        public BenchmarkState() {
            try {

                xmlContent = readFile();
                staxPathFinder = new StaxPathFinder();
                staxPathFinder.disableRawTextReading();
                ToStringBuilderHandler testHandler = new ToStringBuilderHandler(builder);
                staxPathFinder.addXpath("/root/row/registered").onText(testHandler);
//                staxPathFinder.addXpath("/root/row/greeting").onText(testHandler);
//                staxPathFinder.addXpath("/root/row/latitude").onText(testHandler);
                staxPathFinder.addXpath("/root/row/tags").onText(testHandler);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Benchmark
    public void testBigXml(BenchmarkState benchmarkState, final Blackhole blackhole) {
        blackhole.consume(benchmarkState.staxPathFinder.find(benchmarkState.xmlContent));
        if (benchmarkState.builder.length() != 1191873) {
            throw new IllegalStateException(String.format("Length was %d but should be 1191873", benchmarkState.builder.length()));
        }
        benchmarkState.builder.setLength(0);
    }

    private static String readFile() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                BigStaxPathFinderBenchmark.class.getClassLoader().getResourceAsStream("bigjson.xml")));
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
