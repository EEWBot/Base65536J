package net.eewbot.base65536j.exception;

public class BufferTooSmallException extends Base65536Exception {
    public BufferTooSmallException(int expected, int actual) {
        super("Expected buffer length was " + expected + " or more, but actually " + actual + ".");
    }
}
