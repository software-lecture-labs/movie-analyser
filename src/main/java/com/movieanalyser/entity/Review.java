package com.movieanalyser.entity;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;

/**
 * A user's rating and comment for a movie, stored in the "reviews" collection.
 * A user can review a movie only once; this is enforced by a unique index on (userId, movieId).
 */
@Entity("reviews")
public class Review {

    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 10;
    public static final int MAX_COMMENT_LENGTH = 1000;

    @Id
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
    private Instant createdAt;

    @Column
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

        Review review = new Review();
        review.userId = userId;
        review.movieId = movieId;
        review.rating = rating;
        review.comment = comment;
        review.createdAt = now;
        review.updatedAt = now;
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
        this.updatedAt = now;
    }

    private static void validateRating(int value) {
        if (value < MIN_RATING || value > MAX_RATING) {
            throw new IllegalArgumentException(
                    "rating must be between " + MIN_RATING + " and " + MAX_RATING + ", was " + value);
        }
    }

    private static void validateComment(String value) {
        if (value != null && value.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException(
                    "comment must be at most " + MAX_COMMENT_LENGTH + " characters");
        }
    }

    private static void requireNotBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
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
