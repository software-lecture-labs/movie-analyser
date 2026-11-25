package com.movieanalyser.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RatingSummaryTest {

    private static final double DELTA = 1e-9;

    @Test
    void emptyHasNoRatings() {
        RatingSummary empty = RatingSummary.empty();

        assertEquals(0.0, empty.average(), DELTA);
        assertEquals(0, empty.count());
    }

    @Test
    void addToEmpty() {
        RatingSummary result = RatingSummary.empty().add(8);

        assertEquals(8.0, result.average(), DELTA);
        assertEquals(1, result.count());
    }

    @Test
    void addToExisting() {
        RatingSummary result = new RatingSummary(8.0, 1).add(6);

        assertEquals(7.0, result.average(), DELTA);
        assertEquals(2, result.count());
    }

    @Test
    void removeLastResetsToZero() {
        RatingSummary result = new RatingSummary(7.0, 1).remove(7);

        assertEquals(0.0, result.average(), DELTA);
        assertEquals(0, result.count());
    }

    @Test
    void removeFromMany() {
        RatingSummary result = new RatingSummary(7.0, 2).remove(6);

        assertEquals(8.0, result.average(), DELTA);
        assertEquals(1, result.count());
    }

    @Test
    void replaceKeepsCount() {
        RatingSummary result = new RatingSummary(7.0, 2).replace(6, 10);

        assertEquals(9.0, result.average(), DELTA);
        assertEquals(2, result.count());
    }

    @Test
    void removeFromEmptyIsRejected() {
        RatingSummary empty = RatingSummary.empty();

        assertThrows(IllegalStateException.class, () -> empty.remove(5));
    }

    @Test
    void replaceOnEmptyIsRejected() {
        RatingSummary empty = RatingSummary.empty();

        assertThrows(IllegalStateException.class, () -> empty.replace(5, 6));
    }

    @Test
    void negativeCountIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RatingSummary(5.0, -1));
    }
}
