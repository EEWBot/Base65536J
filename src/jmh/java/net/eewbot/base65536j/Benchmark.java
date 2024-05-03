package net.eewbot.base65536j;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class Benchmark {
    private static final Base65536Encoder encoder = Base65536.getEncoder();
    private static final Base65536Decoder decoder = Base65536.getDecoder();
    private static byte[] oneByteArray;
    private static byte[] tenKilobytesArray;
    private static byte[] oneMegabytesArray;
    private static String oneByteString;
    private static String tenKilobytesString;
    private static String oneMegabytesString;

    @Setup
    public void setup() {
        oneByteArray = new byte[]{127};
        tenKilobytesArray = new byte[10_000];
        oneMegabytesArray = new byte[1_000_000];
        for (int i = 0; i < 1_000_000; i++) {
            tenKilobytesArray[i % 100] = (byte) (i & 0xff);
            oneMegabytesArray[i] = (byte) (i & 0xff);
        }
        oneByteString = encoder.encodeToString(oneByteArray);
        tenKilobytesString = encoder.encodeToString(tenKilobytesArray);
        oneMegabytesString = encoder.encodeToString(oneMegabytesArray);
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void encoderOneByte(Blackhole blackhole) {
        blackhole.consume(encoder.encodeToString(oneByteArray));
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void encoderTenKilobytes(Blackhole blackhole) {
        blackhole.consume(encoder.encodeToString(tenKilobytesArray));
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void encoderOneMegabytes(Blackhole blackhole) {
        blackhole.consume(encoder.encodeToString(oneMegabytesArray));
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void decoderOneByte(Blackhole blackhole) {
        blackhole.consume(decoder.decode(oneByteString));
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void decoderTenKilobytes(Blackhole blackhole) {
        blackhole.consume(decoder.decode(tenKilobytesString));
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void decoderOneMegabytes(Blackhole blackhole) {
        blackhole.consume(decoder.decode(oneMegabytesString));
    }
}
