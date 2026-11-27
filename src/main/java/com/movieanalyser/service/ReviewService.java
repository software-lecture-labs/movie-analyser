package com.movieanalyser.service;

import com.movieanalyser.entity.Review;
import jakarta.data.page.Page;

/**
 * Adding, changing and deleting reviews. Every change also updates the movie's
 * average rating and rating count in the same transaction.
 */
public interface ReviewService {

    /**
     * Adds a review of a movie by a user.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException if an id is malformed or the movie does not exist
     * @throws com.movieanalyser.exception.BusinessRuleException if the rating or comment breaks a rule
     * @throws com.movieanalyser.exception.ConflictException if the user already reviewed the movie
     */
    Review addReview(String userId, String movieId, int rating, String comment);

    /**
     * Changes the rating and comment of a review.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException if the id is malformed or the review does not exist
     * @throws com.movieanalyser.exception.BusinessRuleException if the rating or comment breaks a rule
     */
    Review updateReview(String reviewId, int rating, String comment);

    /**
     * Deletes a review.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException if the id is malformed or the review does not exist
     */
    void deleteReview(String reviewId);

    /**
     * @throws com.movieanalyser.exception.ResourceNotFoundException id bozuksa ya da yorum yoksa
     */
    Review getReview(String reviewId);

    /**
     * Bir filmin yorumları, en yeniden eskiye, sayfa sayfa.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException film id'si bozuksa
     * @throws com.movieanalyser.exception.BusinessRuleException sayfa bilgisi geçersizse
     */
    Page<Review> listByMovie(String movieId, int page, int size);

    /**
     * Bir kullanıcının yorumları, en yeniden eskiye, sayfa sayfa.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException kullanıcı id'si bozuksa
     * @throws com.movieanalyser.exception.BusinessRuleException sayfa bilgisi geçersizse
     */
    Page<Review> listByUser(String userId, int page, int size);
}
