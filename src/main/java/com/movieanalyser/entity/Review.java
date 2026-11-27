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
 * Bir kullanıcının bir filme verdiği puan ve yorum; "reviews" koleksiyonunda saklanır.
 * Bir kullanıcı bir filme yalnızca bir kez yorum yapabilir;
 * bu, (userId, movieId) üzerindeki benzersiz indeksle sağlanır.
 */
@Entity("reviews")
public class Review {

    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 10;
    public static final int MAX_COMMENT_LENGTH = 1000;

    // Kodda String olarak görünür, MongoDB'de ObjectId olarak saklanır; böylece id ile arama eşleşir
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

    // Örtük public parametresiz constructor, eşleme katmanı entity'yi yeniden oluştururken kullanır.

    /**
     * Yeni bir yorum oluşturur. Yorum kaydedilene kadar id null kalır.
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
     * Puanı ve yorumu değiştirir. Yeni değerler geçersizse hiçbir şey değişmez.
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
     * MongoDB tarihleri milisaniye hassasiyetinde saklar; saatler ise (örneğin
     * Linux'ta Instant.now()) mikro veya nanosaniye döndürebilir. Burada kesmek,
     * bellekteki değerin veritabanından okunan değerle aynı kalmasını sağlar.
     */
    private static Instant toStoredPrecision(Instant value) {
        return value.truncatedTo(ChronoUnit.MILLIS);
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
