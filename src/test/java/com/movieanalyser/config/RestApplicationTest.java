package com.movieanalyser.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.ApplicationPath;
import org.junit.jupiter.api.Test;

class RestApplicationTest {

    @Test
    void applicationPath_isApi() {
        ApplicationPath path = RestApplication.class.getAnnotation(ApplicationPath.class);

        assertEquals("/api", path.value());
    }
}
