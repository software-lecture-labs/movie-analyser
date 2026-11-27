package com.movieanalyser.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class MovieTest {

    @Test
    void newMovieStartsWithNoRatings() {
        Movie movie = Movie.create(
                "Inception",
                "Christopher Nolan",
                2010,
                "A thief enters dreams.",
                "https://example.com/inception.jpg");

        assertNull(movie.getId());
        assertEquals("Inception", movie.getTitle());
        assertEquals("Christopher Nolan", movie.getDirector());
        assertEquals(2010, movie.getReleaseYear());
        assertEquals(0, movie.getRatingCount());
        assertEquals(0.0, movie.getAverageRating());
    }

    @Test
    void createStoresLowerCaseTitleForSearch() {
        Movie movie = Movie.create("The Matrix", "Wachowski", 1999, null, null);

        assertEquals("The Matrix", movie.getTitle());
        assertEquals("the matrix", movie.getTitleLower());
    }

    @Test
    void normalizeTitleIgnoresCaseAndSurroundingSpaces() {
        assertEquals("the matrix", Movie.normalizeTitle("  The MATRIX "));
    }

    @Test
    void normalizeTitleMakesTurkishCapitalDottedIMatchPlainI() {
        // "İ" lower-cases to "i" followed by a combining dot; the dot must not break matching
        assertEquals("istanbul", Movie.normalizeTitle("İSTANBUL"));
        assertEquals("istanbul", Movie.normalizeTitle("istanbul"));
    }

    @Test
    void normalizeTitleKeepsNullAsNull() {
        assertNull(Movie.normalizeTitle(null));
    }

    @Test
    void searchTermAndStoredTitleUseTheSameNormalization() {
        Movie movie = Movie.create("Léon: The Professional", "Luc Besson", 1994, null, null);

        assertEquals(Movie.normalizeTitle("LÉON: the professional"), movie.getTitleLower());
    }
}
