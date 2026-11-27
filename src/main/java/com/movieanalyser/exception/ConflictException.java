package com.movieanalyser.exception;

/**
 * Bir işlem mevcut verilerle çakıştığında fırlatılır; örneğin aynı kullanıcının
 * aynı filme ikinci kez yorum yapması. HTTP 409'a eşlenir.
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
