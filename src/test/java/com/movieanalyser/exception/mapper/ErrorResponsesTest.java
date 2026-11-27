package com.movieanalyser.exception.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.movieanalyser.dto.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import org.junit.jupiter.api.Test;

class ErrorResponsesTest {

    @Test
    void buildsJsonResponseWithErrorBody() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost:8080/movie-analyser/api/movies/1"));

        try (Response response = ErrorResponses.build(Response.Status.NOT_FOUND, "Film bulunamadı", uriInfo)) {
            assertEquals(404, response.getStatus());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            ErrorResponse body = (ErrorResponse) response.getEntity();
            assertEquals(404, body.status());
            assertEquals("Not Found", body.error());
            assertEquals("Film bulunamadı", body.message());
            assertEquals("/movie-analyser/api/movies/1", body.path());
            assertNotNull(body.timestamp());
        }
    }

    @Test
    void pathIsNullWhenUriInfoIsMissing() {
        try (Response response = ErrorResponses.build(Response.Status.CONFLICT, "Çakışma", null)) {
            assertNull(((ErrorResponse) response.getEntity()).path());
        }
    }
}
