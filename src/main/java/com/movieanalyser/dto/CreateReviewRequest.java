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
        @NotBlank(message = "boş olamaz") String userId,
        @NotBlank(message = "boş olamaz") String movieId,
        @Min(value = 1, message = "1 ile 10 arasında olmalı") @Max(value = 10, message = "1 ile 10 arasında olmalı")
        int rating,
        @Size(max = 1000, message = "en fazla {max} karakter olabilir") String comment) {
}
