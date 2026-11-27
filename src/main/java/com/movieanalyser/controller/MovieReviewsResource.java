package com.movieanalyser.controller;

import com.movieanalyser.dto.PageResponse;
import com.movieanalyser.dto.ReviewResponse;
import com.movieanalyser.mapper.PageMapper;
import com.movieanalyser.mapper.ReviewMapper;
import com.movieanalyser.service.ReviewService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
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

    private ReviewService reviews;

    @Inject
    public MovieReviewsResource(ReviewService reviews) {
        this.reviews = reviews;
    }

    /** CDI'nin bu bean için proxy oluşturabilmesi için gerekli. */
    protected MovieReviewsResource() {
        // doğrudan kullanılmaz
    }

    /** Film yoksa 404 (kural serviste). */
    @GET
    public PageResponse<ReviewResponse> list(@PathParam("movieId") String movieId,
                                             @QueryParam("page") String page,
                                             @QueryParam("size") String size) {
        return PageMapper.toResponse(
                reviews.listByMovie(movieId, QueryParams.page(page), QueryParams.size(size)),
                ReviewMapper::toResponse);
    }
}
