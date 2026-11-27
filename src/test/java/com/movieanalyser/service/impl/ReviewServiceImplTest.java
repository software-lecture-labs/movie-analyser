package com.movieanalyser.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.movieanalyser.entity.Review;
import com.movieanalyser.exception.BusinessRuleException;
import com.movieanalyser.exception.ResourceNotFoundException;
import com.movieanalyser.repository.ReviewRepository;
import com.movieanalyser.repository.ReviewTransactionRepository;
import com.movieanalyser.service.MovieService;
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

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    private static final String USER_ID = "6b060c259c09bf675bb8c53e";
    private static final String MOVIE_ID = "6b060c259c09bf675bb8c53f";
    private static final String REVIEW_ID = "6b060c259c09bf675bb8c540";
    private static final Instant NOW = Instant.parse("2026-11-25T10:00:00Z");

    @Mock
    private ReviewTransactionRepository transactions;

    @Mock
    private ReviewRepository reviews;

    @Mock
    private MovieService movies;

    private ReviewServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReviewServiceImpl(transactions, reviews, movies, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void addRejectsInvalidMovieIdWithoutTouchingStore() {
        assertThrows(ResourceNotFoundException.class, () -> service.addReview(USER_ID, "abc", 8, null));

        verifyNoInteractions(transactions, reviews);
    }

    @Test
    void addRejectsInvalidUserIdWithoutTouchingStore() {
        assertThrows(ResourceNotFoundException.class, () -> service.addReview(null, MOVIE_ID, 8, null));

        verifyNoInteractions(transactions, reviews);
    }

    @Test
    void addMapsInvalidRatingToBusinessRuleException() {
        assertThrows(BusinessRuleException.class, () -> service.addReview(USER_ID, MOVIE_ID, 11, null));

        verifyNoInteractions(transactions, reviews);
    }

    @Test
    void addStoresReviewWithClockTimeAndReturnsStoredReview() {
        Review stored = Review.create(USER_ID, MOVIE_ID, 8, "Great", NOW);
        when(transactions.add(any(Review.class))).thenReturn(REVIEW_ID);
        when(reviews.findById(REVIEW_ID)).thenReturn(Optional.of(stored));

        Review result = service.addReview(USER_ID, MOVIE_ID, 8, "Great");

        assertSame(stored, result);
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(transactions).add(captor.capture());
        assertEquals(8, captor.getValue().getRating());
        assertEquals(NOW, captor.getValue().getCreatedAt());
    }

    @Test
    void updateRejectsInvalidIdWithoutTouchingStore() {
        assertThrows(ResourceNotFoundException.class, () -> service.updateReview("not-an-id", 5, null));

        verifyNoInteractions(transactions, reviews);
    }

    @Test
    void updateOfMissingReviewIsNotFound() {
        when(reviews.findById(REVIEW_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateReview(REVIEW_ID, 5, null));

        verifyNoInteractions(transactions);
    }

    @Test
    void updateEditsReviewAndStoresNewValues() {
        Instant created = NOW.minusSeconds(3600);
        Review existing = Review.create(USER_ID, MOVIE_ID, 8, "Great", created);
        when(reviews.findById(REVIEW_ID)).thenReturn(Optional.of(existing));

        Review result = service.updateReview(REVIEW_ID, 4, "Changed my mind");

        verify(transactions).update(REVIEW_ID, 4, "Changed my mind", NOW);
        assertEquals(4, result.getRating());
        assertEquals(created, result.getCreatedAt());
        assertEquals(NOW, result.getUpdatedAt());
    }

    @Test
    void updateMapsInvalidRatingToBusinessRuleException() {
        Review existing = Review.create(USER_ID, MOVIE_ID, 8, "Great", NOW);
        when(reviews.findById(REVIEW_ID)).thenReturn(Optional.of(existing));

        assertThrows(BusinessRuleException.class, () -> service.updateReview(REVIEW_ID, 0, null));

        verifyNoInteractions(transactions);
    }

    @Test
    void deleteDelegatesToStore() {
        service.deleteReview(REVIEW_ID);

        verify(transactions).delete(REVIEW_ID);
    }

    @Test
    void deleteRejectsInvalidIdWithoutTouchingStore() {
        assertThrows(ResourceNotFoundException.class, () -> service.deleteReview(""));

        verifyNoInteractions(transactions, reviews);
    }
}
