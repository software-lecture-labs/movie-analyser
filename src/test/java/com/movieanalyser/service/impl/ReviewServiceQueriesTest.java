package com.movieanalyser.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.movieanalyser.entity.Review;
import com.movieanalyser.exception.BusinessRuleException;
import com.movieanalyser.exception.ResourceNotFoundException;
import com.movieanalyser.repository.ReviewRepository;
import com.movieanalyser.repository.ReviewTransactionRepository;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** ReviewService'in okuma metotları: tek yorum ve sayfalı listeler. */
@ExtendWith(MockitoExtension.class)
class ReviewServiceQueriesTest {

    private static final String USER_ID = "6b060c259c09bf675bb8c53e";
    private static final String MOVIE_ID = "6b060c259c09bf675bb8c53f";
    private static final String REVIEW_ID = "6b060c259c09bf675bb8c540";
    private static final Instant NOW = Instant.parse("2026-11-28T10:00:00Z");

    @Mock
    private ReviewTransactionRepository transactions;

    @Mock
    private ReviewRepository reviews;

    @Mock
    private Page<Review> page;

    private ReviewServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReviewServiceImpl(transactions, reviews, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void getReviewRejectsMalformedIdWithoutTouchingRepository() {
        assertThrows(ResourceNotFoundException.class, () -> service.getReview("abc"));
        verifyNoInteractions(reviews);
    }

    @Test
    void getReviewOfMissingReviewIsNotFound() {
        when(reviews.findById(REVIEW_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getReview(REVIEW_ID));
    }

    @Test
    void getReviewReturnsStoredReview() {
        Review review = Review.create(USER_ID, MOVIE_ID, 7, null, NOW);
        when(reviews.findById(REVIEW_ID)).thenReturn(Optional.of(review));

        assertSame(review, service.getReview(REVIEW_ID));
    }

    @Test
    void listByMovieUsesRequestedPage() {
        when(reviews.findByMovieIdOrderByCreatedAtDescIdDesc(eq(MOVIE_ID), any(PageRequest.class)))
                .thenReturn(page);

        assertSame(page, service.listByMovie(MOVIE_ID, 2, 5));

        ArgumentCaptor<PageRequest> request = ArgumentCaptor.forClass(PageRequest.class);
        verify(reviews).findByMovieIdOrderByCreatedAtDescIdDesc(eq(MOVIE_ID), request.capture());
        assertEquals(2, request.getValue().page());
        assertEquals(5, request.getValue().size());
    }

    @Test
    void listByMovieRejectsMalformedId() {
        assertThrows(ResourceNotFoundException.class, () -> service.listByMovie("abc", 1, 20));
        verifyNoInteractions(reviews);
    }

    @Test
    void listByMovieRejectsInvalidPage() {
        assertThrows(BusinessRuleException.class, () -> service.listByMovie(MOVIE_ID, 1, 0));
        verifyNoInteractions(reviews);
    }

    @Test
    void listByUserUsesRequestedPage() {
        when(reviews.findByUserIdOrderByCreatedAtDescIdDesc(eq(USER_ID), any(PageRequest.class)))
                .thenReturn(page);

        assertSame(page, service.listByUser(USER_ID, 1, 20));
    }

    @Test
    void listByUserRejectsMalformedId() {
        assertThrows(ResourceNotFoundException.class, () -> service.listByUser(null, 1, 20));
        verifyNoInteractions(reviews);
    }
}
