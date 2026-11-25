package com.movieanalyser.repository;

import com.movieanalyser.entity.Review;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import java.util.Optional;

/**
 * Data access for reviews. The implementation is generated at runtime;
 * queries, including their sort order, are derived from the method names.
 *
 * <p>Review lists are paged and sorted newest first, with the id as a tie breaker
 * so that pages stay stable when several reviews share the same timestamp.
 * Matching indexes are declared in {@code MongoIndexes}. The sort order is part of
 * the method name because the @OrderBy annotation is not supported on derived
 * queries by the current mapping implementation.</p>
 */
@Repository
public interface ReviewRepository extends CrudRepository<Review, String> {

    /**
     * One page of the reviews written for a movie, newest first.
     */
    Page<Review> findByMovieIdOrderByCreatedAtDescIdDesc(String movieId, PageRequest pageRequest);

    /**
     * One page of the reviews written by a user, newest first.
     */
    Page<Review> findByUserIdOrderByCreatedAtDescIdDesc(String userId, PageRequest pageRequest);

    /**
     * The review a user wrote for a movie, if any. There is at most one,
     * guaranteed by the unique index on (userId, movieId).
     */
    Optional<Review> findByUserIdAndMovieId(String userId, String movieId);
}
