package net.eewbot.base65536j;

public class Base65536 {
    private static final Base65536Encoder encoder = new Base65536Encoder();
    private static final Base65536Decoder decoder = new Base65536Decoder();

    public static Base65536Encoder getEncoder() {
        return encoder;
    }

    public static Base65536Decoder getDecoder() {
        return decoder;
    }
}
