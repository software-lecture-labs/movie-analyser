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
 * Bir kullanıcının yorumları: {@code /api/users/{userId}/reviews}, en yeniden eskiye.
 */
@Path("/users/{userId}/reviews")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class UserReviewsResource {

    private ReviewService reviews;

    @Inject
    public UserReviewsResource(ReviewService reviews) {
        this.reviews = reviews;
    }

    /** CDI'nin bu bean için proxy oluşturabilmesi için gerekli. */
    protected UserReviewsResource() {
        // doğrudan kullanılmaz
    }

    // Kullanıcı servisi henüz yok; o gelince olmayan kullanıcı için 404 kontrolü eklenecek
    @GET
    public PageResponse<ReviewResponse> list(@PathParam("userId") String userId,
                                             @QueryParam("page") String page,
                                             @QueryParam("size") String size) {
        return PageMapper.toResponse(
                reviews.listByUser(userId, QueryParams.page(page), QueryParams.size(size)),
                ReviewMapper::toResponse);
    }
}
