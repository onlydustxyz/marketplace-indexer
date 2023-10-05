package com.onlydust.marketplace.indexer.domain.exception;

public class NotFound extends Exception {
    public NotFound(final String message) {
        super("F.NOT_FOUND", message, null);
    }
}
