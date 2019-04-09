package com.interview.fluxoftweet.exceptions;

public abstract class BaseRuntimeException extends RuntimeException {

    public BaseRuntimeException() {
        super();
    }

    public BaseRuntimeException(final String message) {
        super(message);
    }

    public BaseRuntimeException(final String message, final Throwable t) {
        super(message, t);
    }

    public BaseRuntimeException(final Throwable t) {
        super(t);
    }
}
