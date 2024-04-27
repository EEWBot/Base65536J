package net.eewbot.base65536j;

import net.eewbot.base65536j.exception.BufferTooSmallException;
import net.eewbot.base65536j.exception.IllegalBase65536TextException;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Base65536Decoder {
    Base65536Decoder() {}

    private static final Map<Integer, Integer> TABLE = new HashMap<Integer, Integer>() {
        {
            for (int i = 0; i < Base65536Encoder.CODES.length; i++) {
                put(Base65536Encoder.CODES[i], i);
            }
        }
    };

    private static int calcBufferLength(String src) {
        int lastCodePoint = src.codePointAt(src.length() - 1);
        Integer leastByte = TABLE.get(lastCodePoint - (lastCodePoint & ((1 << 8) - 1)));
        if (leastByte == null) throw new IllegalBase65536TextException(src.length(), lastCodePoint);

        int srcCodePointCount = src.codePointCount(0, src.length());
        return leastByte == 256 ? srcCodePointCount * 2 - 1 : srcCodePointCount * 2;
    }

    public byte[] decode(byte[] src) {
        return decode(new String(src, StandardCharsets.UTF_8));
    }

    public int decode(byte[] src, byte[] dst) {
        String srcString = new String(src, StandardCharsets.UTF_8);
        int bufferLength = calcBufferLength(srcString);

        if (dst.length < bufferLength) throw new BufferTooSmallException(bufferLength, dst.length);

        byte[] result = decode(srcString);
        System.arraycopy(result, 0, dst, 0, bufferLength);

        return bufferLength;
    }

    public ByteBuffer decode(ByteBuffer buffer) {
        byte[] src = new byte[buffer.remaining()];
        buffer.get(src);
        return ByteBuffer.wrap(decode(src));
    }

    public byte[] decode(String src) {
        if (src.isEmpty()) return new byte[0];

        int srcCodePointCount = src.codePointCount(0, src.length());
        byte[] buffer;
        {
            int lastCodePoint = src.codePointAt(src.length() - 1);

            Integer leastByte = TABLE.get(lastCodePoint - (lastCodePoint & ((1 << 8) - 1)));
            if (leastByte == null) throw new IllegalBase65536TextException(src.length(), lastCodePoint);

            buffer = new byte[leastByte == 256 ? srcCodePointCount * 2 - 1 : srcCodePointCount * 2];
        }

        AtomicInteger atomicI = new AtomicInteger();
        src.codePoints().forEachOrdered(codePoint -> {
            int i = atomicI.getAndIncrement();
            int mostByte = codePoint & ((1 << 8) - 1);

            Integer leastByte = TABLE.get(codePoint - mostByte);
            if (leastByte == null) throw new IllegalBase65536TextException(i + 1, codePoint);

            buffer[i * 2] = (byte) mostByte;
            if (leastByte != 256) {
                buffer[i * 2 + 1] = leastByte.byteValue();
            } else if (i != srcCodePointCount -1) {
                throw new IllegalBase65536TextException("Base65536 sequence exists after padding byte.");
            }
        });

        return buffer;
    }

    public InputStream wrap(InputStream is) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
