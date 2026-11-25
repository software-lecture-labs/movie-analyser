package com.movieanalyser.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.movieanalyser.dto.HealthResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.Test;

class HealthResourceTest {

    @Test
    void health_returnsUpStatus() {
        HealthResponse response = new HealthResource().health();

        assertEquals("UP", response.status());
    }

    @Test
    void health_isMappedToGetOnHealthPath() throws NoSuchMethodException {
        Path path = HealthResource.class.getAnnotation(Path.class);

        assertEquals("/health", path.value());
        assertTrue(HealthResource.class.getMethod("health").isAnnotationPresent(GET.class));
    }
}