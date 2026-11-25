package com.movieanalyser.exception;

/**
 * Thrown when an operation conflicts with existing data, for example a second
 * review of the same movie by the same user. Mapped to HTTP 409.
 */
public class ConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
