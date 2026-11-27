package com.movieanalyser.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Yorum güncelleme isteği; yalnızca puan ve yorum metni değiştirilebilir.
 */
public record UpdateReviewRequest(
        @Min(1) @Max(10) int rating,
        @Size(max = 1000) String comment) {
}
