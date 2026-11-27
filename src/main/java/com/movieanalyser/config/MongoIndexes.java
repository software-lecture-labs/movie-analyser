package com.movieanalyser.config;

import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import java.util.List;
import java.util.Map;

/**
 * Index definitions for the MongoDB collections.
 *
 * <p>Jakarta NoSQL does not create indexes, so they are declared here and
 * applied with the MongoDB driver when the application starts.</p>
 */
public final class MongoIndexes {

    public static final String REVIEWS = "reviews";
    public static final String USERS = "users";
    public static final String MOVIES = "movies";

    private MongoIndexes() {
    }

    /** Returns the indexes to create, grouped by collection name. */
    public static Map<String, List<IndexModel>> definitions() {
        return Map.of(
                REVIEWS, List.of(
                        // A user can review the same movie only once.
                        unique("uk_reviews_user_movie", "userId", "movieId"),
                        // Paged review lists of a movie or a user, newest first; without these
                        // a lookup by movieId alone would scan the whole collection.
                        newestFirst("ix_reviews_movie_created", "movieId"),
                        newestFirst("ix_reviews_user_created", "userId")),
                USERS, List.of(
                        unique("uk_users_username", "username"),
                        unique("uk_users_email", "email")),
                // Film listesi başlığa (aynı başlıkta id'ye) göre sıralı; index olmadan her sayfa bellekte sıralanır
                MOVIES, List.of(new IndexModel(
                        Indexes.ascending("titleLower", "_id"), new IndexOptions().name("ix_movies_title"))));
    }

    private static IndexModel unique(String name, String... fields) {
        return new IndexModel(Indexes.ascending(fields), new IndexOptions().name(name).unique(true));
    }

    /**
     * Equality on the given field, then createdAt descending with _id as a tie breaker,
     * matching the sort order of the paged repository queries.
     */
    private static IndexModel newestFirst(String name, String field) {
        return new IndexModel(
                Indexes.compoundIndex(
                        Indexes.ascending(field),
                        Indexes.descending("createdAt"),
                        Indexes.descending("_id")),
                new IndexOptions().name(name));
    }
}
