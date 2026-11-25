package com.movieanalyser.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

private static Movie newMovie() {
    return Movie.create(
            "Inception",
            "Christopher Nolan",
            2010,
            "A thief enters dreams.",
            "https://example.com/inception.jpg");
}

@Test
void addRatingSetsAverageForFirstRating() {
    Movie movie = newMovie();

    movie.addRating(8);

    assertEquals(1, movie.getRatingCount());
    assertEquals(8.0, movie.getAverageRating(), 0.0001);
}

@Test
void addRatingUpdatesAverageForSeveralRatings() {
    Movie movie = newMovie();

    movie.addRating(8);
    movie.addRating(6);
    movie.addRating(9);

    assertEquals(3, movie.getRatingCount());
    assertEquals(7.6667, movie.getAverageRating(), 0.0001);
}

@Test
void addRatingRejectsRatingOutOfRange() {
    Movie movie = newMovie();

    assertThrows(IllegalArgumentException.class, () -> movie.addRating(0));
    assertThrows(IllegalArgumentException.class, () -> movie.addRating(11));

    assertEquals(0, movie.getRatingCount());
    assertEquals(0.0, movie.getAverageRating(), 0.0001);
}

@Test
void updateRatingRecalculatesAverage() {
    Movie movie = newMovie();
    movie.addRating(8);
    movie.addRating(6);

    movie.updateRating(8, 4);

    assertEquals(2, movie.getRatingCount());
    assertEquals(5.0, movie.getAverageRating(), 0.0001);
}

@Test
void updateRatingRejectsInvalidRatingAndKeepsState() {
    Movie movie = newMovie();
    movie.addRating(8);

    assertThrows(IllegalArgumentException.class, () -> movie.updateRating(8, 11));
    assertThrows(IllegalArgumentException.class, () -> movie.updateRating(0, 5));

    assertEquals(1, movie.getRatingCount());
    assertEquals(8.0, movie.getAverageRating(), 0.0001);
}

@Test
void updateRatingFailsWhenMovieHasNoRatings() {
    Movie movie = newMovie();

    assertThrows(IllegalStateException.class, () -> movie.updateRating(5, 6));
}

@Test
void removeRatingRecalculatesAverage() {
    Movie movie = newMovie();
    movie.addRating(8);
    movie.addRating(6);
    movie.addRating(10);

    movie.removeRating(10);

    assertEquals(2, movie.getRatingCount());
    assertEquals(7.0, movie.getAverageRating(), 0.0001);
}

@Test
void removeLastRatingResetsAverageToZero() {
    Movie movie = newMovie();
    movie.addRating(7);

    movie.removeRating(7);

    assertEquals(0, movie.getRatingCount());
    assertEquals(0.0, movie.getAverageRating(), 0.0001);
}

@Test
void removeRatingRejectsInvalidRatingAndKeepsState() {
    Movie movie = newMovie();
    movie.addRating(8);

    assertThrows(IllegalArgumentException.class, () -> movie.removeRating(0));

    assertEquals(1, movie.getRatingCount());
    assertEquals(8.0, movie.getAverageRating(), 0.0001);
}

@Test
void removeRatingFailsWhenMovieHasNoRatings() {
    Movie movie = newMovie();

    assertThrows(IllegalStateException.class, () -> movie.removeRating(5));
}
}