package com.movieanalyser.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.movieanalyser.exception.BusinessRuleException;
import jakarta.data.page.PageRequest;
import org.junit.jupiter.api.Test;

class PagingTest {

    @Test
    void buildsPageRequestFromOneBasedPage() {
        PageRequest request = Paging.of(2, 10);

        assertEquals(2, request.page());
        assertEquals(10, request.size());
    }

    @Test
    void acceptsBoundaries() {
        assertEquals(1, Paging.of(1, 1).size());
        assertEquals(Paging.MAX_SIZE, Paging.of(1, Paging.MAX_SIZE).size());
    }

    @Test
    void rejectsPageBelowOne() {
        assertThrows(BusinessRuleException.class, () -> Paging.of(0, 10));
    }

    @Test
    void rejectsSizeBelowOne() {
        assertThrows(BusinessRuleException.class, () -> Paging.of(1, 0));
    }

    @Test
    void rejectsSizeAboveMaximum() {
        assertThrows(BusinessRuleException.class, () -> Paging.of(1, Paging.MAX_SIZE + 1));
    }
}
