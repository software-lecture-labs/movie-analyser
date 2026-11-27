package com.movieanalyser.exception.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.movieanalyser.dto.ErrorResponse;
import com.movieanalyser.exception.BusinessRuleException;
import com.movieanalyser.exception.ConflictException;
import com.movieanalyser.exception.ResourceNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DomainExceptionMappersTest {

    private UriInfo uriInfo;

    @BeforeEach
    void setUp() {
        uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost/movie-analyser/api/reviews"));
    }

    @Test
    void resourceNotFoundIsMappedTo404() {
        try (Response response = new ResourceNotFoundExceptionMapper(uriInfo)
                .toResponse(new ResourceNotFoundException("Film bulunamadı"))) {

            assertError(response, 404, "Not Found", "Film bulunamadı");
        }
    }

    @Test
    void conflictIsMappedTo409() {
        try (Response response = new ConflictExceptionMapper(uriInfo)
                .toResponse(new ConflictException("Bu filme zaten yorum yaptınız"))) {

            assertError(response, 409, "Conflict", "Bu filme zaten yorum yaptınız");
        }
    }

    @Test
    void businessRuleIsMappedTo400() {
        try (Response response = new BusinessRuleExceptionMapper(uriInfo)
                .toResponse(new BusinessRuleException("Puan 1 ile 10 arasında olmalı"))) {

            assertError(response, 400, "Bad Request", "Puan 1 ile 10 arasında olmalı");
        }
    }

    @Test
    void mappersAreRegisteredAsProviders() {
        assertTrue(ResourceNotFoundExceptionMapper.class.isAnnotationPresent(Provider.class));
        assertTrue(ConflictExceptionMapper.class.isAnnotationPresent(Provider.class));
        assertTrue(BusinessRuleExceptionMapper.class.isAnnotationPresent(Provider.class));
    }

    private static void assertError(Response response, int status, String error, String message) {
        assertEquals(status, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertEquals(status, body.status());
        assertEquals(error, body.error());
        assertEquals(message, body.message());
        assertEquals("/movie-analyser/api/reviews", body.path());
    }
}
