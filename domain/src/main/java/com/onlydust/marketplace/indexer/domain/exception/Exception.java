package com.onlydust.marketplace.indexer.domain.exception;

import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public abstract class Exception extends RuntimeException {
    @NonNull
    String code;

    protected Exception(final @NonNull String code, final @NonNull String message, final Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public Boolean isFunctional() {
        return this.code.startsWith("F.");
    }

    public Boolean isTechnical() {
        return this.code.startsWith("T.");
    }
}
