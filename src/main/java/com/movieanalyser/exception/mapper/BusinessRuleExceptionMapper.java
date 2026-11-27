package com.movieanalyser.exception.mapper;

import com.movieanalyser.exception.BusinessRuleException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * İş kuralı ihlali (örn. 1-10 dışındaki puan) için 400 döner.
 */
@Provider
public class BusinessRuleExceptionMapper implements ExceptionMapper<BusinessRuleException> {

    private final UriInfo uriInfo;

    // toResponse yalnızca exception'ı aldığı için UriInfo constructor'da @Context ile alınır;
    // JAX-RS o anki isteği gösteren bir vekil (proxy) verir, testlerde mock geçilebilir.
    public BusinessRuleExceptionMapper(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(BusinessRuleException exception) {
        return ErrorResponses.build(Response.Status.BAD_REQUEST, exception.getMessage(), uriInfo);
    }
}
