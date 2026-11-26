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
    void longAddRemoveCycleStaysExact() {
        RatingSummary summary = RatingSummary.empty().add(3).add(4);

        // Çok sayıda değişiklik, saklanan ortalamada yuvarlama hatası biriktirmemelidir
        for (int i = 0; i < 1000; i++) {
            summary = summary.add(7).add(9).replace(9, 1).remove(7).remove(1);
        }

        assertEquals(3.5, summary.average());
        assertEquals(2, summary.count());
    }

    @Test
    void averageHasNoRoundingErrorAfterRemoval() {
        RatingSummary summary = RatingSummary.empty();
        for (int rating : new int[] {2, 8, 2, 4, 6, 6, 1}) {
            summary = summary.add(rating);
        }

        summary = summary.remove(2);

        // 6 puan üzerinden 8 + 2 + 4 + 6 + 6 + 1 = 27; tam olarak 4.5 olmalı, 4.500000000000001 değil
        assertEquals(4.5, summary.average());
        assertEquals(6, summary.count());
    }

    @Test
    void thirdsAreExactAfterRemoval() {
        RatingSummary summary = RatingSummary.empty().add(10).add(10).add(10).add(4).remove(4);

        assertEquals(10.0, summary.average());
        assertEquals(3, summary.count());
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
