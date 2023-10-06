package com.onlydust.marketplace.indexer.domain.exception;

public class NotFound extends Exception {
    public NotFound(final String message) {
        this(message, null);
    }

    public NotFound(final String message, Throwable cause) {
        super("F.NOT_FOUND", message, cause);
    }
}
