package com.movieanalyser.exception.mapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * İstek DTO'larındaki Bean Validation hatalarını 400'e çevirir. Mesaj "alan: hata" çiftlerinden oluşur,
 * çıktı her seferinde aynı olsun diye sıralanır.
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private final UriInfo uriInfo;

    // toResponse yalnızca exception'ı aldığı için UriInfo constructor'da @Context ile alınır.
    public ConstraintViolationExceptionMapper(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        String message = violations == null ? "" : violations.stream()
                .map(violation -> fieldName(violation.getPropertyPath()) + ": " + violation.getMessage())
                .sorted()
                .collect(Collectors.joining("; "));
        return ErrorResponses.build(Response.Status.BAD_REQUEST, message, uriInfo);
    }

    // "create.arg0.title" gibi bir yolun yalnızca son düğümü (alan adı) istemci için anlamlıdır.
    private static String fieldName(Path path) {
        String name = null;
        for (Path.Node node : path) {
            name = node.getName();
        }
        return name == null ? path.toString() : name;
    }
}
