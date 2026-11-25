package com.movieanalyser.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.movieanalyser.config.MongoIndexes;
import com.movieanalyser.entity.EntityIds;
import com.movieanalyser.entity.Review;
import com.movieanalyser.exception.ConflictException;
import com.movieanalyser.exception.NotFoundException;
import com.movieanalyser.support.MongoTestSupport;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReviewTransactionRepositoryIT {

    private static final double DELTA = 1e-9;
    private static final Instant NOW = Instant.parse("2026-11-25T10:00:00Z");

    private static MongoClient client;
    private static MongoDatabase database;

    private ReviewTransactionRepository repository;
    private MongoCollection<Document> reviews;
    private MongoCollection<Document> movies;
    private ObjectId movieId;

    @BeforeAll
    static void connect() {
        client = MongoTestSupport.client();
        database = MongoTestSupport.database(client);
        // The unique index on (userId, movieId) is what turns a second review into a conflict
        MongoIndexes.definitions().forEach((name, indexes) -> database.getCollection(name).createIndexes(indexes));
    }

    @AfterAll
    static void disconnect() {
        client.close();
    }

    @BeforeEach
    void createMovie() {
        repository = new ReviewTransactionRepository(client, database);
        reviews = database.getCollection("reviews");
        movies = database.getCollection("movies");
        movieId = new ObjectId();
        movies.insertOne(new Document("_id", movieId).append("title", "Test movie")
                .append("averageRating", 0.0).append("ratingCount", 0));
    }

    @AfterEach
    void removeTestData() {
        reviews.deleteMany(Filters.eq("movieId", movieId.toHexString()));
        movies.deleteOne(Filters.eq("_id", movieId));
    }

    @Test
    void addStoresReviewAndUpdatesMovie() {
        String id = repository.add(review(newUserId(), 8));

        assertTrue(EntityIds.isValid(id));
        Document stored = reviews.find(Filters.eq("_id", new ObjectId(id))).first();
        assertNotNull(stored);
        assertEquals(8, stored.getInteger("rating"));
        assertEquals(Date.from(NOW), stored.getDate("createdAt"));
        assertMovie(8.0, 1);
    }

    @Test
    void duplicateReviewIsConflictAndMovieUnchanged() {
        String userId = newUserId();
        repository.add(review(userId, 8));
        Review second = review(userId, 2);

        assertThrows(ConflictException.class, () -> repository.add(second));

        assertMovie(8.0, 1);
        assertEquals(1, reviews.countDocuments(Filters.eq("movieId", movieId.toHexString())));
    }

    @Test
    void missingMovieRollsBackReviewInsert() {
        String missingMovieId = new ObjectId().toHexString();
        Review review = Review.create(newUserId(), missingMovieId, 7, null, NOW);

        assertThrows(NotFoundException.class, () -> repository.add(review));

        // The review was inserted before the movie lookup failed; the abort removed it
        assertEquals(0, reviews.countDocuments(Filters.eq("movieId", missingMovieId)));
    }

    @Test
    void updateReplacesRatingInAverage() {
        repository.add(review(newUserId(), 8));
        String id = repository.add(review(newUserId(), 6));

        repository.update(id, 10, "changed my mind", NOW.plusSeconds(60));

        assertMovie(9.0, 2);
        Document stored = reviews.find(Filters.eq("_id", new ObjectId(id))).first();
        assertEquals(10, stored.getInteger("rating"));
        assertEquals("changed my mind", stored.getString("comment"));
        assertEquals(Date.from(NOW.plusSeconds(60)), stored.getDate("updatedAt"));
        assertEquals(Date.from(NOW), stored.getDate("createdAt"));
    }

    @Test
    void deleteRemovesRatingFromAverage() {
        repository.add(review(newUserId(), 8));
        String id = repository.add(review(newUserId(), 6));

        repository.delete(id);

        assertMovie(8.0, 1);
        assertNull(reviews.find(Filters.eq("_id", new ObjectId(id))).first());
    }

    @Test
    void deleteLastReviewResetsMovie() {
        String id = repository.add(review(newUserId(), 9));

        repository.delete(id);

        assertMovie(0.0, 0);
    }

    @Test
    void updateOfMissingReviewIsNotFound() {
        String missingId = new ObjectId().toHexString();
        Instant later = NOW.plusSeconds(60);

        assertThrows(NotFoundException.class, () -> repository.update(missingId, 5, null, later));
        assertMovie(0.0, 0);
    }

    @Test
    void deleteOfMissingReviewIsNotFound() {
        String missingId = new ObjectId().toHexString();

        assertThrows(NotFoundException.class, () -> repository.delete(missingId));
    }

    @Test
    void concurrentAddsKeepCountAndAverageCorrect() throws Exception {
        List<Callable<String>> tasks = new ArrayList<>();
        for (int rating = 1; rating <= 10; rating++) {
            Review review = review(newUserId(), rating);
            tasks.add(() -> repository.add(review));
        }
        // All ten transactions change the same movie document at the same time
        try (ExecutorService pool = Executors.newFixedThreadPool(10)) {
            for (Future<String> result : pool.invokeAll(tasks)) {
                assertTrue(EntityIds.isValid(result.get()));
            }
        }

        // 1 + 2 + ... + 10 = 55, so the average is 5.5 if no update was lost
        assertMovie(5.5, 10);
    }

    private Review review(String userId, int rating) {
        return Review.create(userId, movieId.toHexString(), rating, "comment " + rating, NOW);
    }

    private static String newUserId() {
        return new ObjectId().toHexString();
    }

    private void assertMovie(double expectedAverage, int expectedCount) {
        Document movie = movies.find(Filters.eq("_id", movieId)).first();
        assertNotNull(movie);
        assertEquals(expectedAverage, ((Number) movie.get("averageRating")).doubleValue(), DELTA);
        assertEquals(expectedCount, ((Number) movie.get("ratingCount")).intValue());
    }
}
