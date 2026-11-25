package com.movieanalyser.support;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Connects integration tests to the MongoDB started by docker compose.
 *
 * <p>Reads the same DB_* environment variables as the application. When a variable
 * is missing, the value from .env.example is used, which is what CI starts with.</p>
 *
 * <p>Testler uygulamanın veritabanını değil, yanına açılan ayrı bir test veritabanını
 * ({@code DB_NAME + "_test"}) kullanır; böylece yarıda kalan bir test geliştiricinin
 * kendi verisinde iz bırakmaz. Kullanıcı uygulama veritabanında tanımlı olduğu için
 * kimlik doğrulama orada yapılır.</p>
 */
public final class MongoTestSupport {

    private MongoTestSupport() {
    }

    /** Opens a new client; the caller closes it. */
    public static MongoClient client() {
        String url = "mongodb://" + env("DB_USER", "movieanalyser") + ":" + env("DB_PASSWORD", "change-me")
                + "@" + env("DB_HOST", "localhost") + ":" + env("DB_PORT", "27017")
                + "/" + applicationDatabaseName() + "?authSource=" + applicationDatabaseName()
                + "&directConnection=true";
        return MongoClients.create(url);
    }

    /** Testlerin kullandığı veritabanı; uygulama veritabanından ayrıdır. */
    public static MongoDatabase database(MongoClient client) {
        return client.getDatabase(testDatabaseName());
    }

    /** Uygulamanın veritabanı; testler buna yazmaz, yalnızca giriş için kullanılır. */
    public static String applicationDatabaseName() {
        return env("DB_NAME", "movieanalyser");
    }

    /** Entegrasyon testlerinin veritabanı. */
    public static String testDatabaseName() {
        return applicationDatabaseName() + "_test";
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
