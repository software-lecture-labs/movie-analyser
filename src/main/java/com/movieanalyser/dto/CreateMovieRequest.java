package com.movieanalyser.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Yeni film isteği. Biçim kuralları burada (400 ve alan adıyla hata mesajı),
 * yılın üst sınırı gibi zamana bağlı kurallar serviste kontrol edilir.
 */
public record CreateMovieRequest(
        @NotBlank(message = "boş olamaz") @Size(max = 200, message = "en fazla {max} karakter olabilir") String title,
        @NotBlank(message = "boş olamaz") @Size(max = 100, message = "en fazla {max} karakter olabilir")
        String director,
        @Min(value = 1888, message = "en az {value} olmalı") int releaseYear,
        @Size(max = 2000, message = "en fazla {max} karakter olabilir") String description,
        @Size(max = 500, message = "en fazla {max} karakter olabilir") String posterUrl) {
}
