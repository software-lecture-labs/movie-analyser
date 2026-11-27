package com.movieanalyser.mapper;

import com.movieanalyser.dto.ReviewResponse;
import com.movieanalyser.entity.Review;

/**
 * Yorum entity'sini istemciye dönen DTO'ya çevirir.
 */
public final class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUserId(),
                review.getMovieId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt());
    }
}
