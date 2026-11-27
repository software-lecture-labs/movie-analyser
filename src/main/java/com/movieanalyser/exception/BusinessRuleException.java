package com.movieanalyser.exception;

/**
 * Girdi bir iş kuralını çiğnediğinde fırlatılır; örneğin 1-10 dışındaki puan. HTTP 400'e eşlenir.
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
