package com.movieanalyser.mapper;

import com.movieanalyser.dto.PageResponse;
import jakarta.data.page.Page;
import java.util.function.Function;

/**
 * Jakarta Data sayfasını, içindeki öğeleri DTO'ya çevirerek {@link PageResponse}'a dönüştürür.
 */
public final class PageMapper {

    private PageMapper() {
    }

    public static <E, T> PageResponse<T> toResponse(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.content().stream().map(mapper).toList(),
                page.pageRequest().page(),
                page.pageRequest().size(),
                page.totalElements(),
                page.totalPages());
    }
}
