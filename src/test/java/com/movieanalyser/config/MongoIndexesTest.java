package com.movieanalyser.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mongodb.client.model.IndexModel;
import java.util.List;
import java.util.Set;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

class MongoIndexesTest {

    @Test
    void reviewsHasUniqueUserMovieIndex() {
        IndexModel index = findByName(MongoIndexes.REVIEWS, "uk_reviews_user_movie");

        // Bileşik indekste alan sırası önemlidir; JSON biçimi bu sırayı korur.
        assertEquals(new Document("userId", 1).append("movieId", 1).toJson(), keysOf(index));
        assertTrue(index.getOptions().isUnique());
    }

    @Test
    void reviewsHasMovieListingIndexSortedByNewest() {
        IndexModel index = findByName(MongoIndexes.REVIEWS, "ix_reviews_movie_created");

        // movieId üzerinde eşitlik, ardından listeleme sırası (en yeni önce, eşitlikte id)
        assertEquals(new Document("movieId", 1).append("createdAt", -1).append("_id", -1).toJson(),
                keysOf(index));
        assertNotEquals(Boolean.TRUE, index.getOptions().isUnique());
    }

    @Test
    void reviewsHasUserListingIndexSortedByNewest() {
        IndexModel index = findByName(MongoIndexes.REVIEWS, "ix_reviews_user_created");

        assertEquals(new Document("userId", 1).append("createdAt", -1).append("_id", -1).toJson(),
                keysOf(index));
        assertNotEquals(Boolean.TRUE, index.getOptions().isUnique());
    }

    @Test
    void usersHasUniqueUsernameIndex() {
        IndexModel index = findByName(MongoIndexes.USERS, "uk_users_username");

        assertEquals(new Document("username", 1).toJson(), keysOf(index));
        assertTrue(index.getOptions().isUnique());
    }

    @Test
    void usersHasUniqueEmailIndex() {
        IndexModel index = findByName(MongoIndexes.USERS, "uk_users_email");

        assertEquals(new Document("email", 1).toJson(), keysOf(index));
        assertTrue(index.getOptions().isUnique());
    }

    @Test
    void moviesHasTitleIndexForSortedListing() {
        IndexModel index = findByName(MongoIndexes.MOVIES, "ix_movies_title");

        // Film listesi başlığa, aynı başlıklarda id'ye göre sıralanıyor
        assertEquals(new Document("titleLower", 1).append("_id", 1).toJson(), keysOf(index));
    }

    @Test
    void moviesHasRatingIndexForBestRatedListing() {
        IndexModel index = findByName(MongoIndexes.MOVIES, "ix_movies_rating");

        assertEquals(new Document("averageRating", -1).append("ratingCount", -1).append("_id", 1).toJson(),
                keysOf(index));
    }

    @Test
    void moviesHasYearIndexForNewestListing() {
        IndexModel index = findByName(MongoIndexes.MOVIES, "ix_movies_year");

        assertEquals(new Document("releaseYear", -1).append("titleLower", 1).append("_id", 1).toJson(),
                keysOf(index));
    }

    @Test
    void definitionsCoverOnlyKnownCollections() {
        assertEquals(Set.of(MongoIndexes.REVIEWS, MongoIndexes.USERS, MongoIndexes.MOVIES),
                MongoIndexes.definitions().keySet());
    }

    private static IndexModel findByName(String collection, String name) {
        List<IndexModel> indexes = MongoIndexes.definitions().get(collection);
        return indexes.stream()
                .filter(index -> name.equals(index.getOptions().getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Index not found: " + name));
    }

    private static String keysOf(IndexModel index) {
        Bson keys = index.getKeys();
        return keys.toBsonDocument().toJson();
    }
}
