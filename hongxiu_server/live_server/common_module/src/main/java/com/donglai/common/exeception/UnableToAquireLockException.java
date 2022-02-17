package com.donglai.common.exeception;

public class UnableToAquireLockException extends RuntimeException {

    public UnableToAquireLockException() {
        super();
    }

    public UnableToAquireLockException(String message) {
        super(message);
    }

    public UnableToAquireLockException(String message, Throwable cause) {
        super(message, cause);
    }
}