package com.movieanalyser.dto;

import java.time.Instant;

/**
 * İstemciye dönen yorum bilgisi.
 */
public record ReviewResponse(
        String id,
        String userId,
        String movieId,
        int rating,
        String comment,
        Instant createdAt,
        Instant updatedAt) {
}
