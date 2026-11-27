package com.movieanalyser.exception.mapper;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

/**
 * İstek gövdesi okunamadığında (bozuk JSON, alan tipinin uymaması) 500 yerine 400 dönülmesini sağlar.
 * JSON-B okuyucusu bu durumda ProcessingException fırlatır ve JAX-RS bunu kendiliğinden 500 yapar.
 * Interceptor yalnızca istek gövdesi okunurken çalıştığı için yanıt yazarken çıkan hatalar 500 kalır.
 */
@Provider
public class RequestBodyReadInterceptor implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException {
        try {
            return context.proceed();
        } catch (ProcessingException exception) {
            throw new BadRequestException("İstek gövdesi okunamadı", exception);
        }
    }
}
