package com.movieanalyser.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class DomainExceptionsTest {

    @Test
    void resourceNotFoundKeepsMessageAndIsUnchecked() {
        RuntimeException e = new ResourceNotFoundException("Movie not found: 42");

        assertEquals("Movie not found: 42", e.getMessage());
        assertInstanceOf(RuntimeException.class, e);
    }

    @Test
    void conflictKeepsMessageAndIsUnchecked() {
        RuntimeException e = new ConflictException("Review already exists");

        assertEquals("Review already exists", e.getMessage());
        assertInstanceOf(RuntimeException.class, e);
    }

    @Test
    void businessRuleKeepsMessageAndIsUnchecked() {
        RuntimeException e = new BusinessRuleException("rating must be between 1 and 10");

        assertEquals("rating must be between 1 and 10", e.getMessage());
        assertInstanceOf(RuntimeException.class, e);
    }

    @Test
    void causeIsKeptWhenGiven() {
        IllegalArgumentException cause = new IllegalArgumentException("bad");

        assertEquals(cause, new BusinessRuleException("invalid", cause).getCause());
        assertEquals(cause, new ConflictException("duplicate", cause).getCause());
    }
}
