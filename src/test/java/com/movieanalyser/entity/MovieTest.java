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
}
