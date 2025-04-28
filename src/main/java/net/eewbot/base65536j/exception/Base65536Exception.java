package net.eewbot.base65536j.exception;

/**
 * The base class of errors in Base65536J.
 */
public abstract class Base65536Exception extends RuntimeException {
    public Base65536Exception(String message) {
        super(message);
    }
}
