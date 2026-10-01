package com.homeforge.auth.exception;

import java.time.Duration;

/** Demasiados intentos de acceso; la API responde 429 con Retry-After. */
public class TooManyAttemptsException extends RuntimeException {

    private final Duration retryAfter;

    public TooManyAttemptsException(String message, Duration retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public Duration getRetryAfter() {
        return retryAfter;
    }
}
