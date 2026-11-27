package com.movieanalyser.exception;

/**
 * İstenen kaynak (kullanıcı, film, yorum) mevcut olmadığında fırlatılır. HTTP 404'e eşlenir.
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
