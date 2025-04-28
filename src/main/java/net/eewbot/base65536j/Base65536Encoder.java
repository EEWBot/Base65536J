package net.eewbot.base65536j;

import net.eewbot.base65536j.exception.BufferTooSmallException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * This class implements an encoder for encoding byte data using the Base65536 encoding scheme follows the
 * <a href="https://github.com/qntm/base65536">original Base65536 implementation</a>.<br>
 * Instances of {@link Base65536Encoder} class are safe for use by multiple concurrent threads.<br>
 * Unless otherwise noted, passing a null argument to a method of this class will cause a {@link NullPointerException}
 * to be thrown.
 */
public class Base65536Encoder {
    Base65536Encoder() {}

    static final int PAD = 5376;
    static final int[] CODES = {
        0x3400, 0x3500, 0x3600, 0x3700, 0x3800, 0x3900, 0x3a00, 0x3b00,
        0x3c00, 0x3d00, 0x3e00, 0x3f00, 0x4000, 0x4100, 0x4200, 0x4300,
        0x4400, 0x4500, 0x4600, 0x4700, 0x4800, 0x4900, 0x4a00, 0x4b00,
        0x4c00, 0x4e00, 0x4f00, 0x5000, 0x5100, 0x5200, 0x5300, 0x5400,
        0x5500, 0x5600, 0x5700, 0x5800, 0x5900, 0x5a00, 0x5b00, 0x5c00,
        0x5d00, 0x5e00, 0x5f00, 0x6000, 0x6100, 0x6200, 0x6300, 0x6400,
        0x6500, 0x6600, 0x6700, 0x6800, 0x6900, 0x6a00, 0x6b00, 0x6c00,
        0x6d00, 0x6e00, 0x6f00, 0x7000, 0x7100, 0x7200, 0x7300, 0x7400,
        0x7500, 0x7600, 0x7700, 0x7800, 0x7900, 0x7a00, 0x7b00, 0x7c00,
        0x7d00, 0x7e00, 0x7f00, 0x8000, 0x8100, 0x8200, 0x8300, 0x8400,
        0x8500, 0x8600, 0x8700, 0x8800, 0x8900, 0x8a00, 0x8b00, 0x8c00,
        0x8d00, 0x8e00, 0x8f00, 0x9000, 0x9100, 0x9200, 0x9300, 0x9400,
        0x9500, 0x9600, 0x9700, 0x9800, 0x9900, 0x9a00, 0x9b00, 0x9c00,
        0x9d00, 0x9e00, 0xa100, 0xa200, 0xa300, 0xa500, 0x10600, 0x12000,
        0x12100, 0x12200, 0x13000, 0x13100, 0x13200, 0x13300, 0x14400, 0x14500,
        0x16800, 0x16900, 0x20000, 0x20100, 0x20200, 0x20300, 0x20400, 0x20500,
        0x20600, 0x20700, 0x20800, 0x20900, 0x20a00, 0x20b00, 0x20c00, 0x20d00,
        0x20e00, 0x20f00, 0x21000, 0x21100, 0x21200, 0x21300, 0x21400, 0x21500,
        0x21600, 0x21700, 0x21800, 0x21900, 0x21a00, 0x21b00, 0x21c00, 0x21d00,
        0x21e00, 0x21f00, 0x22000, 0x22100, 0x22200, 0x22300, 0x22400, 0x22500,
        0x22600, 0x22700, 0x22800, 0x22900, 0x22a00, 0x22b00, 0x22c00, 0x22d00,
        0x22e00, 0x22f00, 0x23000, 0x23100, 0x23200, 0x23300, 0x23400, 0x23500,
        0x23600, 0x23700, 0x23800, 0x23900, 0x23a00, 0x23b00, 0x23c00, 0x23d00,
        0x23e00, 0x23f00, 0x24000, 0x24100, 0x24200, 0x24300, 0x24400, 0x24500,
        0x24600, 0x24700, 0x24800, 0x24900, 0x24a00, 0x24b00, 0x24c00, 0x24d00,
        0x24e00, 0x24f00, 0x25000, 0x25100, 0x25200, 0x25300, 0x25400, 0x25500,
        0x25600, 0x25700, 0x25800, 0x25900, 0x25a00, 0x25b00, 0x25c00, 0x25d00,
        0x25e00, 0x25f00, 0x26000, 0x26100, 0x26200, 0x26300, 0x26400, 0x26500,
        0x26600, 0x26700, 0x26800, 0x26900, 0x26a00, 0x26b00, 0x26c00, 0x26d00,
        0x26e00, 0x26f00, 0x27000, 0x27100, 0x27200, 0x27300, 0x27400, 0x27500,
        0x27600, 0x27700, 0x27800, 0x27900, 0x27a00, 0x27b00, 0x27c00, 0x27d00,
        0x27e00, 0x27f00, 0x28000, 0x28100, 0x28200, 0x28300, 0x28400, 0x28500
    };

    /**
     * Encodes all bytes from the specified byte array into a newly-allocated byte array using the {@link Base65536}
     * encoding scheme. The returned byte array is of the length of the resulting bytes.
     * @param src the byte array to encode
     * @return A newly-allocated byte array containing the resulting encoded bytes.
     */
    public byte[] encode(byte[] src) {
        return encodeToString(src).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Encodes all bytes from the specified byte array using the {@link Base65536} encoding scheme,
     * writing the resulting bytes to the given output byte array, starting at offset 0.<br>
     * It is the responsibility of the invoker of this method to make sure the output byte array dst has enough space
     * for encoding all bytes from the input byte array. No bytes will be written to the output byte array if the output
     * byte array is not big enough.
     * @param src the byte array to encode
     * @param dst the output byte array
     * @return The number of bytes written to the output byte array
     * @throws BufferTooSmallException if dst does not have enough space for encoding all input bytes.
     */
    public int encode(byte[] src, byte[] dst) {
        byte[] result = encode(src);
        if (dst.length < result.length) throw new BufferTooSmallException(result.length, dst.length);
        System.arraycopy(result, 0, dst, 0, result.length);
        return result.length;
    }

    /**
     * Encodes all remaining bytes from the specified byte buffer into a newly-allocated ByteBuffer using the
     * {@link Base65536} encoding scheme. Upon return, the source buffer's position will be updated to its limit;
     * its limit will not have been changed. The returned output buffer's position will be zero and its limit will be
     * the number of resulting encoded bytes.
     * @param buffer the source ByteBuffer to encode
     * @return A newly-allocated byte buffer containing the encoded bytes.
     */
    public ByteBuffer encode(ByteBuffer buffer) {
        byte[] src = new byte[buffer.remaining()];
        buffer.get(src);
        return ByteBuffer.wrap(encode(src));
    }

    /**
     * Encodes the specified byte array into a String using the {@link Base65536} encoding scheme.<br>
     * @param src the byte array to encode
     * @return A string containing the resulting Base65536 encoded characters.
     */
    public String encodeToString(byte[] src) {
        if (src.length == 0) return "";

        int[] codePoints = new int[src.length / 2 + src.length % 2];
        int i = 0;

        for (; i < src.length - 2; i += 2) {
            int mostByte = Byte.toUnsignedInt(src[i]);
            int leastByteIndex = Byte.toUnsignedInt(src[i + 1]);
            codePoints[i / 2] = CODES[leastByteIndex] + mostByte;
        }

        int mostByte = Byte.toUnsignedInt(src[i]);
        int leastByte = i + 1 < src.length ? CODES[Byte.toUnsignedInt(src[i + 1])] : PAD;
        codePoints[i / 2] = leastByte + mostByte;

        return new String(codePoints, 0, codePoints.length);
    }

    /**
     * Not yet implemented.
     * @param os Not yet implemented.
     * @return Not yet implemented.
     * @throws UnsupportedOperationException Always throw this because not yet implemented.
     */
    public OutputStream wrap(OutputStream os) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
