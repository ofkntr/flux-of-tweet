package com.interview.fluxoftweet.exceptions;

public class TweetNotFoundException extends BaseRuntimeException {
    public TweetNotFoundException() {
        super();
    }
    public TweetNotFoundException(final String message) {
        super(message);
    }
}
