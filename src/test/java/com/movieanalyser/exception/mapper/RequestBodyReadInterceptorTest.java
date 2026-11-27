package com.movieanalyser.exception.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class RequestBodyReadInterceptorTest {

    private final RequestBodyReadInterceptor interceptor = new RequestBodyReadInterceptor();

    @Test
    void unreadableBodyBecomesBadRequest() throws IOException {
        ReaderInterceptorContext context = mock(ReaderInterceptorContext.class);
        ProcessingException cause = new ProcessingException("Error deserializing object from entity stream.");
        when(context.proceed()).thenThrow(cause);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> interceptor.aroundReadFrom(context));

        assertEquals(400, exception.getResponse().getStatus());
        assertSame(cause, exception.getCause());
    }

    @Test
    void readableBodyIsReturnedUnchanged() throws IOException {
        ReaderInterceptorContext context = mock(ReaderInterceptorContext.class);
        Object body = new Object();
        when(context.proceed()).thenReturn(body);

        assertSame(body, interceptor.aroundReadFrom(context));
    }

    @Test
    void interceptorIsRegisteredAsProvider() {
        assertTrue(RequestBodyReadInterceptor.class.isAnnotationPresent(Provider.class));
    }
}
