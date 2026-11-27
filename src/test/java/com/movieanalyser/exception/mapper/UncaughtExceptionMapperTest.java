package com.movieanalyser.exception.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.movieanalyser.dto.ErrorResponse;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.junit.jupiter.api.Test;

class UncaughtExceptionMapperTest {

    private final UncaughtExceptionMapper mapper = new UncaughtExceptionMapper(null);

    @Test
    void unexpectedExceptionBecomes500WithGenericMessage() {
        IllegalStateException exception = new IllegalStateException("mongodb://kullanici:sifre@db gizli ayrıntı");
        try (Response response = mapper.toResponse(exception)) {
            assertEquals(500, response.getStatus());
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
            ErrorResponse body = (ErrorResponse) response.getEntity();
            assertEquals("Internal Server Error", body.error());
            assertEquals("Beklenmeyen bir hata oluştu", body.message());
            assertFalse(body.message().contains("gizli"));
        }
    }

    @Test
    void webApplicationExceptionKeepsItsStatus() {
        try (Response response = mapper.toResponse(new WebApplicationException(404))) {
            assertEquals(404, response.getStatus());
            ErrorResponse body = (ErrorResponse) response.getEntity();
            assertEquals(404, body.status());
            assertEquals("Not Found", body.error());
        }
    }

    @Test
    void webApplicationExceptionMessageIsReplacedWithGenericOne() {
        try (Response response = mapper.toResponse(new WebApplicationException("iç ayrıntı", 400))) {
            assertEquals(400, response.getStatus());
            assertFalse(((ErrorResponse) response.getEntity()).message().contains("iç ayrıntı"));
        }
    }

    @Test
    void methodNotAllowedKeeps405AndAllowHeader() {
        try (Response response = mapper.toResponse(new NotAllowedException("GET"))) {
            assertEquals(405, response.getStatus());
            assertEquals("Method Not Allowed", ((ErrorResponse) response.getEntity()).error());
            assertTrue(response.getHeaderString(HttpHeaders.ALLOW).contains("GET"));
            assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        }
    }

    @Test
    void mapperIsRegisteredAsProvider() {
        assertTrue(UncaughtExceptionMapper.class.isAnnotationPresent(Provider.class));
    }
}
