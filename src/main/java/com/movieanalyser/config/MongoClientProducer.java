package com.movieanalyser.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * MongoDB sürücü nesnelerini CDI bean'i olarak sunar.
 *
 * <p>Jakarta NoSQL kendi istemcisini içeride tutar ve dışarı açmaz.
 * Bu yüzden onun kapsamadığı işlemler (indeks oluşturma ve ileride oturum tabanlı
 * çok belgeli transaction'lar) düz sürücüdeki bu istemciyle yapılır.
 * Jakarta NoSQL repository'leriyle yapılan yazmalar sürücü oturumuna katılmaz;
 * bu nedenle transaction gerektiren yazmalar bu istemci üzerinden yapılmalıdır.
 * Jakarta NoSQL ile aynı bağlantı ayarlarını okur.</p>
 */
@ApplicationScoped
public class MongoClientProducer {

    private final String url;
    private final String database;

    @Inject
    public MongoClientProducer(
            @ConfigProperty(name = "jnosql.mongodb.url") String url,
            @ConfigProperty(name = "jnosql.document.database") String database) {
        this.url = url;
        this.database = database;
    }

    /** Normal scope'lu bu bean için proxy oluşturabilmesi adına CDI tarafından gereklidir. */
    protected MongoClientProducer() {
        this(null, null);
    }

    @Produces
    @ApplicationScoped
    public MongoClient mongoClient() {
        // Uygulama başına tek istemci: bağlantı havuzunu o tutar.
        return MongoClients.create(url);
    }

    @Produces
    @ApplicationScoped
    public MongoDatabase mongoDatabase(MongoClient client) {
        return client.getDatabase(database);
    }

    public void closeMongoClient(@Disposes MongoClient client) {
        client.close();
    }
}
