package com.movieanalyser.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Yeni film isteği. Biçim kuralları burada (400 ve alan adıyla hata mesajı),
 * yılın üst sınırı gibi zamana bağlı kurallar serviste kontrol edilir.
 */
public record CreateMovieRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 100) String director,
        @Min(1888) int releaseYear,
        @Size(max = 2000) String description,
        @Size(max = 500) String posterUrl) {
}
