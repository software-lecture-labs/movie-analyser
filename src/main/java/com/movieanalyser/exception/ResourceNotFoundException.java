package com.movieanalyser.exception;

/**
 * Thrown when a requested resource (user, movie, review) does not exist. Mapped to HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
