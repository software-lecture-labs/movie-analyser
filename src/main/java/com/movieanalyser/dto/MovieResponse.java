package com.movieanalyser.dto;

/**
 * İstemciye dönen film bilgisi; ortalama puan ve yorum sayısı dahil.
 */
public record MovieResponse(
        String id,
        String title,
        String director,
        int releaseYear,
        String description,
        String posterUrl,
        double averageRating,
        int ratingCount) {
}
