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
    final
    Integer status;

    private OnlyDustException(final @NonNull Integer status, final @NonNull String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public static OnlyDustException notFound(@NonNull String message) {
        return new OnlyDustException(404, message, null);
    }

    public static OnlyDustException unAuthorized(@NonNull String message, Throwable cause) {
        return new OnlyDustException(401, message, cause);
    }

    public static OnlyDustException internalServerError(@NonNull String message) {
        return internalServerError(message, null);
    }

    public static OnlyDustException internalServerError(@NonNull String message, Throwable cause) {
        return new OnlyDustException(500, message, cause);
    }
}
