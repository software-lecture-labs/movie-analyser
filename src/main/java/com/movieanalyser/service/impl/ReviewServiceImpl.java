package com.movieanalyser.service.impl;

import com.movieanalyser.entity.EntityIds;
import com.movieanalyser.entity.Review;
import com.movieanalyser.exception.BusinessRuleException;
import com.movieanalyser.exception.ResourceNotFoundException;
import com.movieanalyser.repository.ReviewRepository;
import com.movieanalyser.repository.ReviewTransactionRepository;
import com.movieanalyser.service.MovieService;
import com.movieanalyser.service.ReviewService;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Clock;
import java.util.function.Supplier;

/**
 * Girdiyi kontrol eder, yorum kurallarını uygular ve yazma işlemlerini, her birini tek
 * transaction içinde çalıştıran {@link ReviewTransactionRepository}'ye devreder.
 */
@ApplicationScoped
public class ReviewServiceImpl implements ReviewService {

    private ReviewTransactionRepository transactions;
    private ReviewRepository reviews;
    private MovieService movies;
    private Clock clock;

    @Inject
    public ReviewServiceImpl(ReviewTransactionRepository transactions, ReviewRepository reviews,
                             MovieService movies, Clock clock) {
        this.transactions = transactions;
        this.reviews = reviews;
        this.movies = movies;
        this.clock = clock;
    }

    /** Application scope'lu bu bean için proxy oluşturabilmesi adına CDI tarafından gereklidir. */
    protected ReviewServiceImpl() {
        // doğrudan kullanılmaz
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
        PageRequest request = Paging.of(page, size);
        // Olmayan bir filmin yorumları istenirse boş liste değil 404 dönsün
        movies.getMovie(movieId);
        return reviews.findByMovieIdOrderByCreatedAtDescIdDesc(movieId, request);
    }

    @Override
    public Page<Review> listByUser(String userId, int page, int size) {
        requireValidId(userId, "User");
        return reviews.findByUserIdOrderByCreatedAtDescIdDesc(userId, Paging.of(page, size));
    }

    /** Hatalı biçimli bir id hiçbir kayıtlı entity'ye ait olamaz; bu yüzden "bulunamadı" olarak bildirilir. */
    private static void requireValidId(String id, String entityName) {
        if (!EntityIds.isValid(id)) {
            throw new ResourceNotFoundException(entityName + " not found: " + id);
        }
    }

    /** Çiğnenen bir entity kuralını (örneğin 1-10 dışındaki puan) doğrulama hatasına çevirir. */
    private static Review applyRules(Supplier<Review> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException(e.getMessage(), e);
        }
    }
}
