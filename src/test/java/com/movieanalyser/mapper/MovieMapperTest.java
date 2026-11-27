package com.movieanalyser.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.movieanalyser.dto.MovieResponse;
import com.movieanalyser.entity.Movie;
import org.junit.jupiter.api.Test;

class MovieMapperTest {

    @Test
    void copiesAllPublicFields() {
        Movie movie = Movie.create("Inception", "Christopher Nolan", 2010, "Rüya içinde rüya", "https://x/p.jpg");

        MovieResponse response = MovieMapper.toResponse(movie);

        assertNull(response.id());
        assertEquals("Inception", response.title());
        assertEquals("Christopher Nolan", response.director());
        assertEquals(2010, response.releaseYear());
        assertEquals("Rüya içinde rüya", response.description());
        assertEquals("https://x/p.jpg", response.posterUrl());
        assertEquals(0.0, response.averageRating());
        assertEquals(0, response.ratingCount());
    }
}
