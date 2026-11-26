package com.movieanalyser.support;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Entegrasyon testlerini docker compose ile başlatılan MongoDB'ye bağlar.
 *
 * <p>Uygulamayla aynı DB_* ortam değişkenlerini okur. Bir değişken tanımlı değilse
 * .env.example içindeki değer kullanılır; CI de bu değerlerle başlar.</p>
 */
public final class MongoTestSupport {

    private MongoTestSupport() {
    }

    /** Yeni bir istemci açar; kapatma sorumluluğu çağıranındır. */
    public static MongoClient client() {
        String url = "mongodb://" + env("DB_USER", "movieanalyser") + ":" + env("DB_PASSWORD", "change-me")
                + "@" + env("DB_HOST", "localhost") + ":" + env("DB_PORT", "27017")
                + "/" + databaseName() + "?authSource=" + databaseName() + "&directConnection=true";
        return MongoClients.create(url);
    }

    /** Verilen istemcideki uygulama veritabanı. */
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
