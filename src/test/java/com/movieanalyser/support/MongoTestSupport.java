package com.movieanalyser.support;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Entegrasyon testlerini docker compose ile başlatılan MongoDB'ye bağlar.
 *
 * <p>Uygulamayla aynı DB_* ortam değişkenlerini okur. Bir değişken tanımlı değilse
 * .env.example içindeki değer kullanılır; CI de bu değerlerle başlar.</p>
 *
 * <p>Testler uygulamanın veritabanını değil, yanına açılan ayrı bir test veritabanını
 * ({@code DB_NAME + "_test"}) kullanır; böylece yarıda kalan bir test geliştiricinin
 * kendi verisinde iz bırakmaz. Bağlantı, yalnızca test veritabanında yetkili ayrı bir
 * test kullanıcısıyla ({@code DB_TEST_USER}) yapılır; uygulama kullanıcısı kullanılmaz.</p>
 */
public final class MongoTestSupport {

    private MongoTestSupport() {
    }

    /** Yeni bir istemci açar; kapatma sorumluluğu çağıranındır. */
    public static MongoClient client() {
        // Test kullanıcısı test veritabanında tanımlı, kimlik doğrulama da orada yapılır
        String url = "mongodb://" + testUserName() + ":" + env("DB_TEST_PASSWORD", "change-me-test")
                + "@" + env("DB_HOST", "localhost") + ":" + env("DB_PORT", "27017")
                + "/" + testDatabaseName() + "?authSource=" + testDatabaseName()
                + "&directConnection=true";
        return MongoClients.create(url);
    }

    /** Testlerin kullandığı veritabanı; uygulama veritabanından ayrıdır. */
    public static MongoDatabase database(MongoClient client) {
        return client.getDatabase(testDatabaseName());
    }

    /** Uygulamanın veritabanı; testler buna bağlanmaz. */
    public static String applicationDatabaseName() {
        return env("DB_NAME", "movieanalyser");
    }

    /** Entegrasyon testlerinin veritabanı. */
    public static String testDatabaseName() {
        return applicationDatabaseName() + "_test";
    }

    /** Uygulamanın kullanıcısı; testler bu kullanıcıyla bağlanmaz. */
    public static String applicationUserName() {
        return env("DB_USER", "movieanalyser");
    }

    /** Yalnızca test veritabanında yetkili test kullanıcısı. */
    public static String testUserName() {
        return env("DB_TEST_USER", "movieanalyser_test");
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
