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

    private MongoIndexes() {
    }

    /** Returns the indexes to create, grouped by collection name. */
    public static Map<String, List<IndexModel>> definitions() {
        return Map.of(
                // A user can review the same movie only once.
                REVIEWS, List.of(unique("uk_reviews_user_movie", "userId", "movieId")),
                USERS, List.of(
                        unique("uk_users_username", "username"),
                        unique("uk_users_email", "email")));
    }

    private static IndexModel unique(String name, String... fields) {
        return new IndexModel(Indexes.ascending(fields), new IndexOptions().name(name).unique(true));
    }
}
