package com.movieanalyser.repository;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.movieanalyser.entity.RatingSummary;
import com.movieanalyser.entity.Review;
import com.movieanalyser.exception.ConflictException;
import com.movieanalyser.exception.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 * Writes reviews and keeps the movie's average rating and rating count in step,
 * each operation in a single MongoDB transaction.
 *
 * <p>Repository writes of the mapping layer do not join a driver session, so this
 * class uses the driver directly. Documents are written in the same shape the
 * mapping layer reads: ObjectId as _id and BSON dates for timestamps. When two
 * transactions change the same movie at the same time, MongoDB reports a write
 * conflict and the driver retries the losing transaction, so no update is lost.</p>
 */
@ApplicationScoped
public class ReviewTransactionRepository {

    private static final String ID = "_id";
    private static final String RATING = "rating";
    private static final String COMMENT = "comment";
    private static final String USER_ID = "userId";
    private static final String MOVIE_ID = "movieId";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String AVERAGE_RATING = "averageRating";
    private static final String RATING_COUNT = "ratingCount";

    private MongoClient client;
    private MongoCollection<Document> reviews;
    private MongoCollection<Document> movies;

    @Inject
    public ReviewTransactionRepository(MongoClient client, MongoDatabase database) {
        this.client = client;
        this.reviews = database.getCollection("reviews");
        this.movies = database.getCollection("movies");
    }

    /** Required by CDI to create a proxy for this application scoped bean. */
    protected ReviewTransactionRepository() {
        // not used directly
    }

    /**
     * Stores a new review and adds its rating to the movie.
     *
     * @return the id of the stored review
     * @throws ConflictException if the user already reviewed this movie
     * @throws ResourceNotFoundException if the movie does not exist; nothing is stored
     */
    public String add(Review review) {
        ObjectId id = new ObjectId();
        Document document = new Document(ID, id)
                .append(RATING, review.getRating())
                .append(COMMENT, review.getComment())
                .append(USER_ID, review.getUserId())
                .append(MOVIE_ID, review.getMovieId())
                .append(CREATED_AT, Date.from(review.getCreatedAt()))
                .append(UPDATED_AT, Date.from(review.getUpdatedAt()));
        try {
            inTransaction(session -> {
                reviews.insertOne(session, document);
                // Inserted first on purpose: a missing movie aborts the transaction and the insert is undone
                changeRating(session, review.getMovieId(), summary -> summary.add(review.getRating()));
                return null;
            });
        } catch (MongoWriteException e) {
            if (ErrorCategory.fromErrorCode(e.getCode()) == ErrorCategory.DUPLICATE_KEY) {
                throw new ConflictException("Bu kullanıcı bu filme zaten yorum yapmış", e);
            }
            throw e;
        }
        return id.toHexString();
    }

    /**
     * Changes the rating and comment of a review and replaces the old rating in the movie's average.
     *
     * @throws ResourceNotFoundException if the review or its movie does not exist
     */
    public void update(String reviewId, int rating, String comment, Instant updatedAt) {
        inTransaction(session -> {
            Document current = findReview(session, reviewId);
            int oldRating = current.getInteger(RATING);
            reviews.updateOne(session, byId(reviewId), Updates.combine(
                    Updates.set(RATING, rating),
                    Updates.set(COMMENT, comment),
                    Updates.set(UPDATED_AT, Date.from(updatedAt))));
            changeRating(session, current.getString(MOVIE_ID), summary -> summary.replace(oldRating, rating));
            return null;
        });
    }

    /**
     * Deletes a review and removes its rating from the movie's average.
     *
     * @throws ResourceNotFoundException if the review or its movie does not exist
     */
    public void delete(String reviewId) {
        inTransaction(session -> {
            Document current = findReview(session, reviewId);
            reviews.deleteOne(session, byId(reviewId));
            changeRating(session, current.getString(MOVIE_ID),
                    summary -> summary.remove(current.getInteger(RATING)));
            return null;
        });
    }

    private void inTransaction(Function<ClientSession, Void> work) {
        try (ClientSession session = client.startSession()) {
            // Commits when work returns, aborts when it throws, retries on transient errors
            session.withTransaction(() -> work.apply(session));
        }
    }

    private Document findReview(ClientSession session, String reviewId) {
        Document review = reviews.find(session, byId(reviewId)).first();
        if (review == null) {
            throw new ResourceNotFoundException("Yorum bulunamadı: " + reviewId);
        }
        return review;
    }

    private void changeRating(ClientSession session, String movieId, Function<RatingSummary, RatingSummary> change) {
        Document movie = movies.find(session, byId(movieId)).first();
        if (movie == null) {
            throw new ResourceNotFoundException("Film bulunamadı: " + movieId);
        }
        RatingSummary next = change.apply(new RatingSummary(
                numberOrZero(movie.get(AVERAGE_RATING)).doubleValue(),
                numberOrZero(movie.get(RATING_COUNT)).intValue()));
        movies.updateOne(session, byId(movieId), Updates.combine(
                Updates.set(AVERAGE_RATING, next.average()),
                Updates.set(RATING_COUNT, next.count())));
    }

    private static Number numberOrZero(Object value) {
        return value instanceof Number number ? number : 0;
    }

    private static Bson byId(String id) {
        return Filters.eq(ID, new ObjectId(id));
    }
}
