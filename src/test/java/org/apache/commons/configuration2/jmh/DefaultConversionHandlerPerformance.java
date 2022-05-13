/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.configuration2.jmh;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/** Benchmarks for the {@link DoubleFormat} class.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class DefaultConversionHandlerPerformance {

    private static final int CNT = 1_000;

    /** Benchmark input providing a source of conversion handler input. */
    @State(Scope.Thread)
    public static class ConversionHandlerInput {

        Configuration config;

        /** Set up the instance for the benchmark. */
        @Setup(Level.Iteration)
        public void setup() {
            Properties props = new Properties();
            props.setProperty("x", "a");
            props.setProperty("y", "b");
            props.setProperty("z", "c");

            for (int i = 0; i < CNT; ++i) {
                props.setProperty("test." + i, i + ": ${x} ${y} ${z}");
            }

            config = ConfigurationConverter.getConfiguration(props);
        }
    }

    @Benchmark
    public void interpolateBaseline(final ConversionHandlerInput input, final Blackhole bh) {
        for (int i = 0; i < CNT; ++i) {
            bh.consume("test." + i);
        }
    }

    @Benchmark
    public void interpolate(final ConversionHandlerInput input, final Blackhole bh) {
        final Configuration config = input.config;
        for (int i = 0; i < CNT; ++i) {
            bh.consume(config.getString("test." + i));
        }
    }
}
