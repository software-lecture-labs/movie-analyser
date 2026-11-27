package com.movieanalyser.exception.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.movieanalyser.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ConstraintViolationExceptionMapperTest {

    private final ConstraintViolationExceptionMapper mapper = new ConstraintViolationExceptionMapper(null);

    @Test
    void violationsAreListedAsFieldAndMessageInStableOrder() {
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(
                violation("Puan 1 ile 10 arasında olmalı", "create", "arg0", "rating"),
                violation("boş olamaz", "create", "arg0", "comment")));

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(400, response.getStatus());
            ErrorResponse body = (ErrorResponse) response.getEntity();
            assertEquals("Bad Request", body.error());
            assertEquals("comment: boş olamaz; rating: Puan 1 ile 10 arasında olmalı", body.message());
            assertNull(body.path());
        }
    }

    @Test
    void emptyViolationSetStillReturns400() {
        try (Response response = mapper.toResponse(new ConstraintViolationException("geçersiz", null))) {
            assertEquals(400, response.getStatus());
            assertEquals("", ((ErrorResponse) response.getEntity()).message());
        }
    }

    @SuppressWarnings("unchecked")
    private static ConstraintViolation<Object> violation(String message, String... nodeNames) {
        List<Path.Node> nodes = new ArrayList<>();
        for (String name : nodeNames) {
            Path.Node node = mock(Path.Node.class);
            when(node.getName()).thenReturn(name);
            nodes.add(node);
        }
        Path path = mock(Path.class);
        when(path.iterator()).thenAnswer(invocation -> nodes.iterator());
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(message);
        return violation;
    }
}
