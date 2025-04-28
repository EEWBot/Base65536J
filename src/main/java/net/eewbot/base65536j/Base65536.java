package net.eewbot.base65536j;

/**
 * This class consists exclusively of static methods for obtaining encoders and decoders for the Base65536 encoding
 * scheme.
 * The implementation of this class follows the
 * <a href="https://github.com/qntm/base65536">original Base65536 implementation</a>.
 */
public class Base65536 {
    private static final Base65536Encoder encoder = new Base65536Encoder();
    private static final Base65536Decoder decoder = new Base65536Decoder();

    /**
     * Returns a {@link Base65536Encoder}.
     * @return A base65536 encoder.
     */
    public static Base65536Encoder getEncoder() {
        return encoder;
    }

    /**
     * Returns a {@link Base65536Decoder}.
     * @return A base65536 decoder.
     */
    public static Base65536Decoder getDecoder() {
        return decoder;
    }
}
