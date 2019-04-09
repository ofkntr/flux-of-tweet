package com.interview.fluxoftweet.exceptions;

public class MissingParametterException extends BaseRuntimeException{

    public MissingParametterException() {
        super();
    }
    public MissingParametterException(final String message) {
        super(message);
    }
}
