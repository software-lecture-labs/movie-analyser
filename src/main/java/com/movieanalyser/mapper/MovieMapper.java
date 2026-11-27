package com.movieanalyser.mapper;

import com.movieanalyser.dto.MovieResponse;
import com.movieanalyser.entity.Movie;

/**
 * Film entity'sini istemciye dönen DTO'ya çevirir; entity REST katmanına çıkmaz.
 */
public final class MovieMapper {

    private MovieMapper() {
    }

    public static MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseYear(),
                movie.getDescription(),
                movie.getPosterUrl(),
                movie.getAverageRating(),
                movie.getRatingCount());
    }
}
