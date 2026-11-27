package com.movieanalyser.service.impl;

import com.movieanalyser.entity.EntityIds;
import com.movieanalyser.entity.Review;
import com.movieanalyser.exception.BusinessRuleException;
import com.movieanalyser.exception.ResourceNotFoundException;
import com.movieanalyser.repository.ReviewRepository;
import com.movieanalyser.repository.ReviewTransactionRepository;
import com.movieanalyser.service.ReviewService;
import jakarta.data.page.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Clock;
import java.util.function.Supplier;

/**
 * Checks input, applies the review rules and hands the writes to
 * {@link ReviewTransactionRepository}, which runs each of them in one transaction.
 */
@ApplicationScoped
public class ReviewServiceImpl implements ReviewService {

    private ReviewTransactionRepository transactions;
    private ReviewRepository reviews;
    private Clock clock;

    @Inject
    public ReviewServiceImpl(ReviewTransactionRepository transactions, ReviewRepository reviews, Clock clock) {
        this.transactions = transactions;
        this.reviews = reviews;
        this.clock = clock;
    }

    /** Required by CDI to create a proxy for this application scoped bean. */
    protected ReviewServiceImpl() {
        // not used directly
    }

    @Override
    public Review addReview(String userId, String movieId, int rating, String comment) {
        requireValidId(userId, "User");
        requireValidId(movieId, "Movie");
        Review review = applyRules(() -> Review.create(userId, movieId, rating, comment, clock.instant()));

        String id = transactions.add(review);
        return reviews.findById(id)
                .orElseThrow(() -> new IllegalStateException("Stored review could not be read: " + id));
    }

    @Override
    public Review updateReview(String reviewId, int rating, String comment) {
        requireValidId(reviewId, "Review");
        Review review = reviews.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        applyRules(() -> {
            review.edit(rating, comment, clock.instant());
            return review;
        });

        transactions.update(reviewId, review.getRating(), review.getComment(), review.getUpdatedAt());
        return review;
    }

    @Override
    public void deleteReview(String reviewId) {
        requireValidId(reviewId, "Review");
        transactions.delete(reviewId);
    }

    @Override
    public Review getReview(String reviewId) {
        requireValidId(reviewId, "Review");
        return reviews.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
    }

    @Override
    public Page<Review> listByMovie(String movieId, int page, int size) {
        requireValidId(movieId, "Movie");
        // Sayfa bilgisi veritabanına gitmeden önce doğrulanır
        return reviews.findByMovieIdOrderByCreatedAtDescIdDesc(movieId, Paging.of(page, size));
    }

    @Override
    public Page<Review> listByUser(String userId, int page, int size) {
        requireValidId(userId, "User");
        return reviews.findByUserIdOrderByCreatedAtDescIdDesc(userId, Paging.of(page, size));
    }

    /** A malformed id cannot belong to any stored entity, so it is reported as not found. */
    private static void requireValidId(String id, String entityName) {
        if (!EntityIds.isValid(id)) {
            throw new ResourceNotFoundException(entityName + " not found: " + id);
        }
    }

    /** Turns a broken entity rule (for example a rating outside 1-10) into a validation error. */
    private static Review applyRules(Supplier<Review> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException(e.getMessage(), e);
        }
    }
}
