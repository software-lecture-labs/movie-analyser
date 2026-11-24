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
 * <p>Jakarta NoSQL keeps its own client internally, so index creation and
 * multi-document transactions need a separate client from the plain driver.
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
