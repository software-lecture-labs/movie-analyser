package com.movieanalyser.controller;

import com.movieanalyser.dto.PageResponse;
import com.movieanalyser.dto.ReviewResponse;
import com.movieanalyser.mapper.PageMapper;
import com.movieanalyser.mapper.ReviewMapper;
import com.movieanalyser.service.MovieService;
import com.movieanalyser.service.ReviewService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * Bir filmin yorumları: {@code /api/movies/{movieId}/reviews}, en yeniden eskiye.
 */
@Path("/movies/{movieId}/reviews")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class MovieReviewsResource {

    private MovieService movies;
    private ReviewService reviews;

    @Inject
    public MovieReviewsResource(MovieService movies, ReviewService reviews) {
        this.movies = movies;
        this.reviews = reviews;
    }

    /** CDI'nin bu bean için proxy oluşturabilmesi için gerekli. */
    protected MovieReviewsResource() {
        // doğrudan kullanılmaz
    }

    @GET
    public PageResponse<ReviewResponse> list(@PathParam("movieId") String movieId,
                                             @QueryParam("page") @DefaultValue("1") int page,
                                             @QueryParam("size") @DefaultValue("20") int size) {
        // Olmayan bir filmin yorumları istenirse boş liste değil 404 dönsün
        movies.getMovie(movieId);
        return PageMapper.toResponse(reviews.listByMovie(movieId, page, size), ReviewMapper::toResponse);
    }
}
