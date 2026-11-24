package com.movieanalyser.repository;

import com.movieanalyser.entity.Review;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Data access for reviews. The implementation is generated at runtime;
 * queries are derived from the method names.
 */
@Repository
public interface ReviewRepository extends CrudRepository<Review, String> {

    /**
     * All reviews written for a movie.
     */
    List<Review> findByMovieId(String movieId);

    /**
     * All reviews written by a user.
     */
    List<Review> findByUserId(String userId);

    /**
     * The review a user wrote for a movie, if any. There is at most one,
     * guaranteed by the unique index on (userId, movieId).
     */
    Optional<Review> findByUserIdAndMovieId(String userId, String movieId);
}
