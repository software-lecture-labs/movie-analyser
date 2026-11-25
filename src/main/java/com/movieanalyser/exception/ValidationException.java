package com.movieanalyser.exception;

/**
 * Thrown when input breaks a business rule, for example a rating outside 1-10. Mapped to HTTP 400.
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
