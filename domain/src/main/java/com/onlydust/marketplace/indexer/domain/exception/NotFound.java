package com.onlydust.marketplace.indexer.domain.exception;

public class NotFound extends OnlyDustException {
    public NotFound(final String message) {
        this(message, null);
    }

    public NotFound(final String message, Throwable cause) {
        super(400, message, cause);
    }
}
