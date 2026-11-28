package com.movieanalyser.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Yeni yorum isteği. Biçim kuralları burada (400 ve alan adıyla hata mesajı);
 * aynı kurallar entity'de de korunur.
 */
public record CreateReviewRequest(
        @NotBlank String userId,
        @NotBlank String movieId,
        @Min(1) @Max(10) int rating,
        @Size(max = 1000) String comment) {
}
