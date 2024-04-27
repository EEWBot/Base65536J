package net.eewbot.base65536j.exception;

public class IllegalBase65536TextException extends Base65536Exception {
    public IllegalBase65536TextException(int at, int codePoint) {
        super("Unknown code point at " + at + ": " + codePoint);
    }
    public IllegalBase65536TextException(String message) {
        super(message);
    }
}
