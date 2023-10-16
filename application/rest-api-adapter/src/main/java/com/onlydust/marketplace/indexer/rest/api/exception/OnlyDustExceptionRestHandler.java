package com.onlydust.marketplace.indexer.rest.api.exception;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.rest.api.model.OnlyDustError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@ControllerAdvice
@Slf4j
public class OnlyDustExceptionRestHandler {

    private static OnlyDustError onlyDustErrorFromException(final OnlyDustException exception) {
        final HttpStatus httpStatus = Optional.ofNullable(HttpStatus.resolve(exception.getStatus()))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        final UUID errorId = UUID.randomUUID();
        final OnlyDustError onlyDustError = new OnlyDustError();
        onlyDustError.setStatus(httpStatus.value());
        onlyDustError.setMessage(httpStatus.name());
        onlyDustError.setId(errorId);
        if (httpStatus.is5xxServerError()) {
            LOGGER.error(String.format("Error %s returned from the REST API with stacktrace :", errorId),
                    isNull(exception.getCause()) ? exception : exception.getCause());
        } else {
            LOGGER.warn(String.format("Error %s returned from the REST API with stacktrace :", errorId),
                    isNull(exception.getCause()) ? exception : exception.getCause());
        }
        return onlyDustError;
    }

    @ExceptionHandler({OnlyDustException.class})
    protected ResponseEntity<OnlyDustError> handleOnlyDustException(final OnlyDustException exception) {
        final OnlyDustError onlyDustError = onlyDustErrorFromException(exception);
        return ResponseEntity.status(onlyDustError.getStatus()).body(onlyDustError);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<OnlyDustError> unauthorized(AuthenticationException exception) {
        return handleOnlyDustException(new OnlyDustException(
                HttpStatus.UNAUTHORIZED.value(),
                "Missing authentication",
                exception
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OnlyDustError> internalError(final Exception exception) {
        return handleOnlyDustException(new OnlyDustException(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal error from unexpected runtime exception",
                exception
        ));
    }
}
