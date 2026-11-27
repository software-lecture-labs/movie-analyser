package com.movieanalyser.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.movieanalyser.dto.PageResponse;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import java.util.List;
import org.junit.jupiter.api.Test;

class PageMapperTest {

    @Test
    @SuppressWarnings("unchecked")
    void mapsContentAndPagingInfo() {
        Page<Integer> page = mock(Page.class);
        when(page.content()).thenReturn(List.of(1, 2, 3));
        when(page.pageRequest()).thenReturn(PageRequest.ofPage(2).size(3));
        when(page.totalElements()).thenReturn(8L);
        when(page.totalPages()).thenReturn(3L);

        PageResponse<String> response = PageMapper.toResponse(page, number -> "#" + number);

        assertEquals(List.of("#1", "#2", "#3"), response.content());
        assertEquals(2, response.page());
        assertEquals(3, response.size());
        assertEquals(8L, response.totalElements());
        assertEquals(3L, response.totalPages());
    }
}
