package com.movieanalyser.exception;

/**
 * Thrown when a requested resource (user, movie, review) does not exist. Mapped to HTTP 404.
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
