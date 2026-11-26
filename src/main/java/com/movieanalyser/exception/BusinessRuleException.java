package com.movieanalyser.exception;

/**
 * Thrown when input breaks a business rule, for example a rating outside 1-10. Mapped to HTTP 400.
 */
public class BusinessRuleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
