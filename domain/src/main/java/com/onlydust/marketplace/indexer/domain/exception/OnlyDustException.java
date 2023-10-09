package com.onlydust.marketplace.indexer.domain.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class OnlyDustException extends RuntimeException {
    @NonNull
    Integer status;

    public OnlyDustException(final @NonNull Integer status, final @NonNull String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
