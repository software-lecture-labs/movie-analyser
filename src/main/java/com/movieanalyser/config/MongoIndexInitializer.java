package com.movieanalyser.config;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Uygulama başladığında MongoDB indekslerini oluşturur.
 */
@ApplicationScoped
public class MongoIndexInitializer {

    private static final Logger LOGGER = Logger.getLogger(MongoIndexInitializer.class.getName());

    private final MongoDatabase database;

    @Inject
    public MongoIndexInitializer(MongoDatabase database) {
        this.database = database;
    }

    /** Normal scope'lu bu bean için proxy oluşturabilmesi adına CDI tarafından gereklidir. */
    protected MongoIndexInitializer() {
        this(null);
    }

    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object event) {
        int count = 0;
        for (Map.Entry<String, List<IndexModel>> entry : MongoIndexes.definitions().entrySet()) {
            // createIndexes tekrar çalıştırılabilir: aynı ad ve anahtarlara sahip indeks olduğu gibi bırakılır.
            database.getCollection(entry.getKey()).createIndexes(entry.getValue());
            count += entry.getValue().size();
        }
        final int ensured = count;
        LOGGER.info(() -> "Ensured MongoDB indexes: " + ensured);
    }
}
