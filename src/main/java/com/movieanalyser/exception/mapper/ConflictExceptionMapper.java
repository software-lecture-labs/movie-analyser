package com.movieanalyser.exception.mapper;

import com.movieanalyser.exception.ConflictException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mevcut veriyle çakışan işlem (örn. aynı filme ikinci yorum) için 409 döner.
 */
@Provider
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {

    private final UriInfo uriInfo;

    // toResponse yalnızca exception'ı aldığı için UriInfo constructor'da @Context ile alınır;
    // JAX-RS o anki isteği gösteren bir vekil (proxy) verir, testlerde mock geçilebilir.
    public ConflictExceptionMapper(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(ConflictException exception) {
        return ErrorResponses.build(Response.Status.CONFLICT, exception.getMessage(), uriInfo);
    }
}
