package com.movieanalyser.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ReviewTest {

    private static final String USER_ID = "user-1";
    private static final String MOVIE_ID = "movie-1";
    private static final Instant CREATED = Instant.parse("2026-11-25T10:00:00Z");
    private static final Instant EDITED = Instant.parse("2026-11-26T12:30:00Z");

    @Test
    void createSetsFieldsAndTimestamps() {
        Review review = Review.create(USER_ID, MOVIE_ID, 8, "Great movie", CREATED);

        assertNull(review.getId());
        assertEquals(USER_ID, review.getUserId());
        assertEquals(MOVIE_ID, review.getMovieId());
        assertEquals(8, review.getRating());
        assertEquals("Great movie", review.getComment());
        assertEquals(CREATED, review.getCreatedAt());
        assertEquals(CREATED, review.getUpdatedAt());
    }

    @Test
    void createRejectsRatingBelowOne() {
        assertThrows(IllegalArgumentException.class,
                () -> Review.create(USER_ID, MOVIE_ID, 0, null, CREATED));
    }

    @Test
    void createRejectsRatingAboveTen() {
        assertThrows(IllegalArgumentException.class,
                () -> Review.create(USER_ID, MOVIE_ID, 11, null, CREATED));
    }

    @Test
    void createAcceptsBoundaryRatings() {
        assertEquals(1, Review.create(USER_ID, MOVIE_ID, 1, null, CREATED).getRating());
        assertEquals(10, Review.create(USER_ID, MOVIE_ID, 10, null, CREATED).getRating());
    }

    @Test
    void createRejectsBlankUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> Review.create(" ", MOVIE_ID, 5, null, CREATED));
        assertThrows(IllegalArgumentException.class,
                () -> Review.create(null, MOVIE_ID, 5, null, CREATED));
    }

    @Test
    void createRejectsBlankMovieId() {
        assertThrows(IllegalArgumentException.class,
                () -> Review.create(USER_ID, "", 5, null, CREATED));
        assertThrows(IllegalArgumentException.class,
                () -> Review.create(USER_ID, null, 5, null, CREATED));
    }

    @Test
    void createAcceptsNullComment() {
        assertNull(Review.create(USER_ID, MOVIE_ID, 5, null, CREATED).getComment());
    }

    @Test
    void createAcceptsCommentAtMaxLength() {
        String comment = "a".repeat(Review.MAX_COMMENT_LENGTH);

        assertEquals(comment, Review.create(USER_ID, MOVIE_ID, 5, comment, CREATED).getComment());
    }

    @Test
    void createRejectsTooLongComment() {
        String comment = "a".repeat(Review.MAX_COMMENT_LENGTH + 1);

        assertThrows(IllegalArgumentException.class,
                () -> Review.create(USER_ID, MOVIE_ID, 5, comment, CREATED));
    }

    @Test
    void createRejectsMissingTimestamp() {
        assertThrows(NullPointerException.class,
                () -> Review.create(USER_ID, MOVIE_ID, 5, null, null));
    }

    @Test
    void editUpdatesRatingCommentAndUpdatedAtOnly() {
        Review review = Review.create(USER_ID, MOVIE_ID, 8, "Great movie", CREATED);

        review.edit(4, "Not that great on second watch", EDITED);

        assertEquals(4, review.getRating());
        assertEquals("Not that great on second watch", review.getComment());
        assertEquals(CREATED, review.getCreatedAt());
        assertEquals(EDITED, review.getUpdatedAt());
        assertEquals(USER_ID, review.getUserId());
        assertEquals(MOVIE_ID, review.getMovieId());
    }

    @Test
    void createTruncatesTimestampsToMilliseconds() {
        // Linux'taki saatler mikro veya nanosaniye döndürür; BSON tarihleri milisaniye tutar
        Instant precise = Instant.parse("2026-11-25T10:00:00.123456789Z");
        Instant millis = Instant.parse("2026-11-25T10:00:00.123Z");

        Review review = Review.create(USER_ID, MOVIE_ID, 5, null, precise);

        assertEquals(millis, review.getCreatedAt());
        assertEquals(millis, review.getUpdatedAt());
    }

    @Test
    void editTruncatesUpdatedAtToMilliseconds() {
        Review review = Review.create(USER_ID, MOVIE_ID, 5, null, CREATED);

        review.edit(6, null, Instant.parse("2026-11-26T12:30:00.987654Z"));

        assertEquals(Instant.parse("2026-11-26T12:30:00.987Z"), review.getUpdatedAt());
    }

    @Test
    void editWithoutCommentClearsTheOldComment() {
        // PUT tam değiştirmedir: gövdede comment yoksa eski metin silinir (README'de yazıyor)
        Review review = Review.create(USER_ID, MOVIE_ID, 5, "İlk izlenim", CREATED);

        review.edit(7, null, EDITED);

        assertNull(review.getComment());
        assertEquals(7, review.getRating());
    }

    @Test
    void editRejectsInvalidRatingAndKeepsState() {
        Review review = Review.create(USER_ID, MOVIE_ID, 8, "Great movie", CREATED);

        assertThrows(IllegalArgumentException.class, () -> review.edit(11, "x", EDITED));

        assertEquals(8, review.getRating());
        assertEquals("Great movie", review.getComment());
        assertEquals(CREATED, review.getUpdatedAt());
    }
}
