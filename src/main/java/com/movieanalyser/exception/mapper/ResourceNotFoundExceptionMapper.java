package com.movieanalyser.exception.mapper;

import com.movieanalyser.exception.ResourceNotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Bulunamayan kaynak (kullanıcı, film, yorum) için 404 ve servisin mesajını döner.
 */
@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    private final UriInfo uriInfo;

    // toResponse yalnızca exception'ı aldığı için UriInfo constructor'da @Context ile alınır;
    // JAX-RS o anki isteği gösteren bir vekil (proxy) verir, testlerde mock geçilebilir.
    public ResourceNotFoundExceptionMapper(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        return ErrorResponses.build(Response.Status.NOT_FOUND, exception.getMessage(), uriInfo);
    }
}
