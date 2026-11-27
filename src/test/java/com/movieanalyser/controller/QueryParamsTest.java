package com.movieanalyser.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.movieanalyser.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    void parsesNumber() {
        assertEquals(3, QueryParams.toInt("3", "page", 1));
        assertEquals(7, QueryParams.toInt(" 7 ", "size", 20));
    }

    @Test
    void missingValueUsesDefault() {
        assertEquals(1, QueryParams.toInt(null, "page", 1));
        assertEquals(20, QueryParams.toInt("", "size", 20));
    }

    @Test
    void nonNumericValueIs400NotA404() {
        // JAX-RS sayıya çeviremediği parametreyi 404'e çeviriyordu; burada anlaşılır bir 400 olmalı
        BusinessRuleException e = assertThrows(BusinessRuleException.class,
                () -> QueryParams.toInt("abc", "page", 1));

        assertEquals("page bir tam sayı olmalı, gelen: abc", e.getMessage());
    }

    @Test
    void tooLargeNumberIs400() {
        assertThrows(BusinessRuleException.class, () -> QueryParams.toInt("99999999999", "page", 1));
    }
}
