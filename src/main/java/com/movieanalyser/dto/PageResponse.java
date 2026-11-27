package com.movieanalyser.dto;

import java.util.List;

/**
 * Sayfalı liste yanıtı. {@code page} 1'den başlar.
 *
 * @param <T> listedeki öğe tipi
 */
public record PageResponse<T>(
        List<T> content,
        long page,
        int size,
        long totalElements,
        long totalPages) {
}
