package com.movieanalyser.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Yorum güncelleme isteği; yalnızca puan ve yorum metni değiştirilebilir.
 */
public record UpdateReviewRequest(
        @Min(value = 1, message = "1 ile 10 arasında olmalı") @Max(value = 10, message = "1 ile 10 arasında olmalı")
        int rating,
        @Size(max = 1000, message = "en fazla {max} karakter olabilir") String comment) {
}
