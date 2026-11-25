package com.movieanalyser.entity;

import jakarta.nosql.Column;
import jakarta.nosql.Convert;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import org.eclipse.jnosql.databases.mongodb.mapping.ObjectIdConverter;

/**
 * Kullanıcıların puanlayıp yorumlayabildiği film; "movies" koleksiyonunda saklanır.
 * Ortalama puan ve puan sayısı filmin kendi üzerinde tutulur.
 */
@Entity("movies")
public class Movie {

    // Kodda String olarak görünür, MongoDB'de ObjectId olarak saklanır; böylece id ile arama eşleşir
    @Id
    @Convert(ObjectIdConverter.class)
    private String id;

    @Column
    private String title;

    @Column
    private String director;

    @Column
    private int releaseYear;

    @Column
    private String description;

    @Column
    private String posterUrl;

    @Column
    private double averageRating;

    @Column
    private int ratingCount;

    // Örtük public parametresiz constructor, eşleme katmanı entity'yi yeniden oluştururken kullanır.

    /**
     * Puanı olmayan yeni bir film oluşturur. Film kaydedilene kadar id null kalır.
     */
    public static Movie create(String title, String director, int releaseYear,
                               String description, String posterUrl) {
        Movie movie = new Movie();
        movie.title = title;
        movie.director = director;
        movie.releaseYear = releaseYear;
        movie.description = description;
        movie.posterUrl = posterUrl;
        return movie;
    }
    /**
 * Yeni bir puanı ekler; puan sayısını ve ortalamayı günceller.
 * Puan geçersizse hiçbir şey değişmez.
 */
public void addRating(int rating) {
    validateRating(rating);
    double total = averageRating * ratingCount + rating;
    ratingCount++;
    averageRating = total / ratingCount;
}
/**
 * Bir puanı değiştirir; puan sayısı aynı kalır, ortalama yeniden hesaplanır.
 * Puanlardan biri geçersizse ya da filmin hiç puanı yoksa hiçbir şey değişmez.
 */
public void updateRating(int oldRating, int newRating) {
    validateRating(oldRating);
    validateRating(newRating);
    requireRatings();
    double total = averageRating * ratingCount - oldRating + newRating;
    averageRating = total / ratingCount;
}

private void requireRatings() {
    if (ratingCount == 0) {
        throw new IllegalStateException("movie has no ratings");
    }
}


private static void validateRating(int value) {
    if (value < Review.MIN_RATING || value > Review.MAX_RATING) {
        throw new IllegalArgumentException(
                "rating must be between " + Review.MIN_RATING + " and " + Review.MAX_RATING
                        + ", was " + value);
    }
}


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }
}
