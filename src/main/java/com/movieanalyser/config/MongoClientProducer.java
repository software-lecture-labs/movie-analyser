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
 * Exposes the MongoDB driver objects as CDI beans.
 *
 * <p>Jakarta NoSQL keeps its own client internally and does not expose it,
 * so operations it does not cover (index creation, and later session-based
 * multi-document transactions) use this client from the plain driver.
 * Writes made through Jakarta NoSQL repositories do not join a driver
 * session, so transactional writes must go through this client.
 * It reads the same connection settings as Jakarta NoSQL.</p>
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

    /** Required by CDI to create a proxy for this normal scoped bean. */
    protected MongoClientProducer() {
        this(null, null);
    }

    @Produces
    @ApplicationScoped
    public MongoClient mongoClient() {
        // One client per application: it holds the connection pool.
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
