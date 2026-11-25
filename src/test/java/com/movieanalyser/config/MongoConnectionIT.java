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

            // Multi-document transactions need a replica set with a writable primary
            assertEquals("rs0", hello.get("setName"));
            assertEquals(Boolean.TRUE, hello.get("isWritablePrimary"));
        }
    }
}
