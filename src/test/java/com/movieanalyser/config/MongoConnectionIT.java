package com.movieanalyser.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mongodb.client.MongoClient;
import com.movieanalyser.support.MongoTestSupport;
import java.util.Map;
import org.bson.Document;
import org.junit.jupiter.api.Test;

class MongoConnectionIT {

    @Test
    void replicaSetIsAvailable() {
        try (MongoClient client = MongoTestSupport.client()) {
            Map<String, Object> hello = MongoTestSupport.database(client).runCommand(new Document("hello", 1));

            // Çok belgeli transaction'lar, yazılabilir bir primary'ye sahip replica set gerektirir
            assertEquals("rs0", hello.get("setName"));
            assertEquals(Boolean.TRUE, hello.get("isWritablePrimary"));
        }
    }
}
