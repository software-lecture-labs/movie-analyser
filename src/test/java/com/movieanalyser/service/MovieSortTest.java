package com.movieanalyser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.movieanalyser.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

class MovieSortTest {

    @Test
    void parsesKnownValuesIgnoringCase() {
        assertEquals(MovieSort.TITLE, MovieSort.from("title"));
        assertEquals(MovieSort.RATING, MovieSort.from("Rating"));
        assertEquals(MovieSort.YEAR, MovieSort.from(" YEAR "));
    }

    @Test
    void missingValueMeansTitle() {
        assertEquals(MovieSort.TITLE, MovieSort.from(null));
        assertEquals(MovieSort.TITLE, MovieSort.from("  "));
    }

    @Test
    void unknownValueIsRejectedWithTheOptions() {
        BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> MovieSort.from("popular"));

        assertTrue(e.getMessage().contains("title, rating, year"), e.getMessage());
    }
}
