package com.movieanalyser.support;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Connects integration tests to the MongoDB started by docker compose.
 *
 * <p>Reads the same DB_* environment variables as the application. When a variable
 * is missing, the value from .env.example is used, which is what CI starts with.</p>
 */
public final class MongoTestSupport {

    private MongoTestSupport() {
    }

    /** Opens a new client; the caller closes it. */
    public static MongoClient client() {
        String url = "mongodb://" + env("DB_USER", "movieanalyser") + ":" + env("DB_PASSWORD", "change-me")
                + "@" + env("DB_HOST", "localhost") + ":" + env("DB_PORT", "27017")
                + "/" + databaseName() + "?authSource=" + databaseName() + "&directConnection=true";
        return MongoClients.create(url);
    }

    /** The application database on the given client. */
    public static MongoDatabase database(MongoClient client) {
        return client.getDatabase(databaseName());
    }

    private static String databaseName() {
        return env("DB_NAME", "movieanalyser");
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
