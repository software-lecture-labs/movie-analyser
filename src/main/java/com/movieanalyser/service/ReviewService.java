package com.movieanalyser.service;

import com.movieanalyser.entity.Review;

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
}
