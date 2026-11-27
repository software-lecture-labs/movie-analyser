package com.movieanalyser.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.movieanalyser.entity.Review;
import com.movieanalyser.repository.MovieRepository;
import com.movieanalyser.repository.ReviewRepository;
import com.movieanalyser.repository.ReviewTransactionRepository;
import com.movieanalyser.service.impl.MovieServiceImpl;
import com.movieanalyser.service.impl.ReviewServiceImpl;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * İstemciye giden hata mesajları Türkçe ve tutarlı olmalı; API'de karışık dil görünmesin.
 */
class ErrorMessagesTest {

    private static final String USER_ID = "6b060c259c09bf675bb8c53e";
    private static final String MOVIE_ID = "6b060c259c09bf675bb8c53f";
    private static final Instant NOW = Instant.parse("2026-11-28T10:00:00Z");

    private final MovieServiceImpl movieService =
            new MovieServiceImpl(Mockito.mock(MovieRepository.class), Clock.systemUTC());
    private final ReviewServiceImpl reviewService = new ReviewServiceImpl(
            Mockito.mock(ReviewTransactionRepository.class), Mockito.mock(ReviewRepository.class), Clock.systemUTC());

    @Test
    void movieNotFoundMessage() {
        assertEquals("Film bulunamadı: abc",
                assertThrows(ResourceNotFoundException.class, () -> movieService.getMovie("abc")).getMessage());
    }

    @Test
    void reviewNotFoundMessage() {
        assertEquals("Yorum bulunamadı: abc",
                assertThrows(ResourceNotFoundException.class, () -> reviewService.getReview("abc")).getMessage());
    }

    @Test
    void userNotFoundMessage() {
        assertEquals("Kullanıcı bulunamadı: abc",
                assertThrows(ResourceNotFoundException.class, () -> reviewService.listByUser("abc", 1, 20))
                        .getMessage());
    }

    @Test
    void ratingOutOfRangeMessage() {
        assertEquals("Puan 1 ile 10 arasında olmalı, gelen: 11",
                assertThrows(IllegalArgumentException.class,
                        () -> Review.create(USER_ID, MOVIE_ID, 11, null, NOW)).getMessage());
    }

    @Test
    void commentTooLongMessage() {
        String comment = "a".repeat(Review.MAX_COMMENT_LENGTH + 1);

        assertEquals("Yorum en fazla 1000 karakter olabilir",
                assertThrows(IllegalArgumentException.class,
                        () -> Review.create(USER_ID, MOVIE_ID, 5, comment, NOW)).getMessage());
    }

    @Test
    void blankFieldMessage() {
        assertEquals("userId boş olamaz",
                assertThrows(IllegalArgumentException.class,
                        () -> Review.create(" ", MOVIE_ID, 5, null, NOW)).getMessage());
    }

    @Test
    void ratingRuleReachesClientInTurkish() {
        // Servis entity kuralını 400'e çevirir; mesaj aynen istemciye gider
        assertEquals("Puan 1 ile 10 arasında olmalı, gelen: 0",
                assertThrows(BusinessRuleException.class,
                        () -> reviewService.addReview(USER_ID, MOVIE_ID, 0, null)).getMessage());
    }
}
