package net.eewbot.base65536j;

import net.eewbot.base65536j.exception.BufferTooSmallException;
import net.eewbot.base65536j.exception.IllegalBase65536TextException;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class implements a decoder for decoding byte data using the Base65536 encoding scheme follows the
 * <a href="https://github.com/qntm/base65536">original Base65536 implementation</a>.<br>
 * Instances of {@link Base65536Decoder} class are safe for use by multiple concurrent threads.<br>
 * Unless otherwise noted, passing a null argument to a method of this class will cause a {@link NullPointerException}
 * to be thrown.
 */
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
        int srcCodePointCount = src.codePointCount(0, src.length());
        int offset = src.offsetByCodePoints(0, srcCodePointCount -1);
        int lastCodePoint = src.codePointAt(offset);
        int codeBlock = lastCodePoint - (lastCodePoint & 0xff);

        Integer leastByte = TABLE.get(codeBlock);
        if (leastByte == null && codeBlock != Base65536Encoder.PAD)
            throw new IllegalBase65536TextException(src.length(), lastCodePoint);

        return codeBlock == Base65536Encoder.PAD ? srcCodePointCount * 2 - 1 : srcCodePointCount * 2;
    }

    /**
     * Decodes all bytes from the input byte array using the {@link Base65536} encoding scheme, writing the results into
     * a newly-allocated output byte array. The returned byte array is of the length of the resulting bytes.
     * @param src the byte array to decode
     * @return A newly-allocated byte array containing the decoded bytes.
     * @throws IllegalBase65536TextException if src is not in valid Base65536 scheme.
     */
    public byte[] decode(byte[] src) {
        return decode(new String(src, StandardCharsets.UTF_8));
    }

    /**
     * Decodes all bytes from the input byte array using the {@link Base65536} encoding scheme, writing the results into
     * the given output byte array, starting at offset 0.<br>
     * It is the responsibility of the invoker of this method to make sure the output byte array dst has enough space
     * for decoding all bytes from the input byte array. No bytes will be written to the output byte array if the output
     * byte array is not big enough.
     * @param src the byte array to decode
     * @param dst the output byte array
     * @return The number of bytes written to the output byte array
     * @throws IllegalBase65536TextException if src is not in valid Base65536 scheme.
     * @throws BufferTooSmallException if dst does not have enough space for decoding all input bytes.
     */
    public int decode(byte[] src, byte[] dst) {
        String srcString = new String(src, StandardCharsets.UTF_8);
        int bufferLength = calcBufferLength(srcString);

        if (dst.length < bufferLength) throw new BufferTooSmallException(bufferLength, dst.length);

        byte[] result = decode(srcString);
        System.arraycopy(result, 0, dst, 0, bufferLength);

        return bufferLength;
    }

    /**
     * Decodes all bytes from the input byte buffer using the {@link Base65536} encoding scheme, writing the results
     * into a newly-allocated ByteBuffer.<br>
     * Unpon return, the source buffer's position will be updated to its limit; its limit will not have been changed.
     * The returned output buffer's position will be zero and its limit will be the number of resulting decoded bytes.
     * IllegalBase65536TextException is thrown if the input buffer is not in valid Base65536 encoding scheme.
     * The Position of the input buffer will not be advanced in this case.
     * @param buffer the ByteBuffer to decode
     * @return A newly-allocated byte buffer containing the decoded bytes
     * @throws IllegalBase65536TextException if src is not in valid Base65536 scheme.
     */
    public ByteBuffer decode(ByteBuffer buffer) {
        byte[] src = new byte[buffer.remaining()];
        buffer.get(src);
        return ByteBuffer.wrap(decode(src));
    }

    /**
     * Decodes a Base65536 encoded String into a newly-allocated byte array using the {@link Base65536} encoding scheme.
     * @param src the string to decode
     * @return A newly-allocated byte array containing the decoded bytes.
     * @throws IllegalBase65536TextException if src is not in valid Base65536 scheme
     */
    public byte[] decode(String src) {
        if (src.isEmpty()) return new byte[0];

        int srcCodePointCount = src.codePointCount(0, src.length());
        byte[] buffer = new byte[calcBufferLength(src)];

        AtomicInteger atomicI = new AtomicInteger();
        src.codePoints().forEachOrdered(codePoint -> {
            int i = atomicI.getAndIncrement();
            int mostByte = codePoint & 0xFF;
            int codeBlock = codePoint - mostByte;

            Integer leastByte = TABLE.get(codeBlock);
            if (leastByte == null && codeBlock != Base65536Encoder.PAD)
                throw new IllegalBase65536TextException(i + 1, codePoint);

            buffer[i * 2] = (byte) mostByte;
            if (codeBlock != Base65536Encoder.PAD) {
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
