package com.movieanalyser.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;

class MongoTestSupportTest {

    @Test
    void testDatabaseIsSeparateFromApplicationDatabase() {
        // Entegrasyon testleri geliştiricinin kendi verisine dokunmamalı
        assertNotEquals(MongoTestSupport.applicationDatabaseName(), MongoTestSupport.testDatabaseName());
        assertEquals(MongoTestSupport.applicationDatabaseName() + "_test", MongoTestSupport.testDatabaseName());
    }

    @Test
    void testsConnectToTestDatabase() {
        // İstemci oluşturmak bağlantı açmaz; yalnızca hangi veritabanının seçildiğine bakılıyor
        try (MongoClient client = MongoTestSupport.client()) {
            assertEquals(MongoTestSupport.testDatabaseName(), MongoTestSupport.database(client).getName());
        }
    }
}
