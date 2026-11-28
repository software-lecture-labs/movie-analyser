package com.movieanalyser.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.movieanalyser.dto.ReviewResponse;
import com.movieanalyser.entity.Review;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ReviewMapperTest {

    @Test
    void copiesAllPublicFields() {
        Instant created = Instant.parse("2026-11-28T10:00:00Z");
        Review review = Review.create("6b060c259c09bf675bb8c53e", "6b060c259c09bf675bb8c53f", 8, "Güzel", created);

        ReviewResponse response = ReviewMapper.toResponse(review);

        assertEquals("6b060c259c09bf675bb8c53e", response.userId());
        assertEquals("6b060c259c09bf675bb8c53f", response.movieId());
        assertEquals(8, response.rating());
        assertEquals("Güzel", response.comment());
        assertEquals(created, response.createdAt());
        assertEquals(created, response.updatedAt());
    }
}
