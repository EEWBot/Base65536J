package net.eewbot.base65536j.exception;

/**
 * The error that couldn't decode input because it's not in valid Base65536 scheme.
 */
public class IllegalBase65536TextException extends Base65536Exception {
    public IllegalBase65536TextException(int at, int codePoint) {
        super("Unknown code point at " + at + ": " + codePoint);
    }
    public IllegalBase65536TextException(String message) {
        super(message);
    }
}
