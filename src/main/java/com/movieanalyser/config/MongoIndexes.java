package com.movieanalyser.config;

import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import java.util.List;
import java.util.Map;

/**
 * MongoDB koleksiyonlarının indeks tanımları.
 *
 * <p>Jakarta NoSQL indeks oluşturmaz; bu yüzden indeksler burada tanımlanır ve
 * uygulama başlarken MongoDB sürücüsüyle uygulanır.</p>
 */
public final class MongoIndexes {

    public static final String REVIEWS = "reviews";
    public static final String USERS = "users";
    public static final String MOVIES = "movies";

    private MongoIndexes() {
    }

    /** Oluşturulacak indeksleri koleksiyon adına göre gruplanmış olarak döndürür. */
    public static Map<String, List<IndexModel>> definitions() {
        return Map.of(
                REVIEWS, List.of(
                        // Bir kullanıcı aynı filme yalnızca bir kez yorum yapabilir.
                        unique("uk_reviews_user_movie", "userId", "movieId"),
                        // Bir filmin veya kullanıcının sayfalı yorum listeleri, en yeni önce;
                        // bunlar olmazsa yalnızca movieId ile arama tüm koleksiyonu tarardı.
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
     * Verilen alanda eşitlik, ardından createdAt azalan sırada ve eşitlik durumunda _id ile;
     * sayfalı repository sorgularının sıralamasıyla aynıdır.
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
