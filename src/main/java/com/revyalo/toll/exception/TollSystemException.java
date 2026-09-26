package com.revyalo.toll.exception;

public class TollSystemException extends RuntimeException {
    public TollSystemException(String message) {
        super(message);
    }

    public TollSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
