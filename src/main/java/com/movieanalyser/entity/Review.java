package com.movieanalyser.entity;

import jakarta.nosql.Column;
import jakarta.nosql.Convert;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.eclipse.jnosql.databases.mongodb.mapping.ObjectIdConverter;

/**
 * A user's rating and comment for a movie, stored in the "reviews" collection.
 * A user can review a movie only once; this is enforced by a unique index on (userId, movieId).
 */
@Entity("reviews")
public class Review {

    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 10;
    public static final int MAX_COMMENT_LENGTH = 1000;

    // Exposed as a String, stored as a MongoDB ObjectId so that lookups by id match
    @Id
    @Convert(ObjectIdConverter.class)
    private String id;

    @Column
    @Min(MIN_RATING)
    @Max(MAX_RATING)
    private int rating;

    @Column
    @Size(max = MAX_COMMENT_LENGTH)
    private String comment;

    @Column
    @NotBlank
    private String userId;

    @Column
    @NotBlank
    private String movieId;

    @Column
    @Convert(InstantDateConverter.class)
    private Instant createdAt;

    @Column
    @Convert(InstantDateConverter.class)
    private Instant updatedAt;

    // The implicit public no-arg constructor is used by the mapping layer to rebuild entities.

    /**
     * Creates a new review. The id stays null until the review is saved.
     */
    public static Review create(String userId, String movieId, int rating, String comment, Instant now) {
        requireNotBlank(userId, "userId");
        requireNotBlank(movieId, "movieId");
        validateRating(rating);
        validateComment(comment);
        Objects.requireNonNull(now, "now");

        Instant timestamp = toStoredPrecision(now);
        Review review = new Review();
        review.userId = userId;
        review.movieId = movieId;
        review.rating = rating;
        review.comment = comment;
        review.createdAt = timestamp;
        review.updatedAt = timestamp;
        return review;
    }

    /**
     * Changes the rating and comment. Nothing is changed if the new values are invalid.
     */
    public void edit(int newRating, String newComment, Instant now) {
        validateRating(newRating);
        validateComment(newComment);
        Objects.requireNonNull(now, "now");

        this.rating = newRating;
        this.comment = newComment;
        this.updatedAt = toStoredPrecision(now);
    }

    /**
     * MongoDB stores dates with millisecond precision, while clocks (for example
     * Instant.now() on Linux) may return micro or nanoseconds. Truncating here keeps
     * the in-memory value equal to the value read back from the database.
     */
    private static Instant toStoredPrecision(Instant value) {
        return value.truncatedTo(ChronoUnit.MILLIS);
    }

    private static void validateRating(int value) {
        if (value < MIN_RATING || value > MAX_RATING) {
            throw new IllegalArgumentException(
                    "Puan " + MIN_RATING + " ile " + MAX_RATING + " arasında olmalı, gelen: " + value);
        }
    }

    private static void validateComment(String value) {
        if (value != null && value.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException(
                    "Yorum en fazla " + MAX_COMMENT_LENGTH + " karakter olabilir");
        }
    }

    private static void requireNotBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " boş olamaz");
        }
    }

    public String getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getUserId() {
        return userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
