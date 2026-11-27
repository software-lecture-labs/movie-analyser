package com.movieanalyser.exception.mapper;

import com.movieanalyser.dto.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.time.Instant;

/**
 * ExceptionMapper'ların ortak yardımcısı: hata gövdesini ve JSON yanıtını tek yerde kurar,
 * böylece her mapper aynı kodu tekrar etmez.
 */
public final class ErrorResponses {

    private ErrorResponses() {
    }

    /** Verilen durum kodu ve mesajla JSON hata yanıtı oluşturur. */
    public static Response build(Response.StatusType status, String message, UriInfo uriInfo) {
        return Response.status(status)
                .entity(body(status, message, uriInfo))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    /** Hata gövdesini oluşturur; UriInfo yoksa (örn. birim testte) path null kalır. */
    public static ErrorResponse body(Response.StatusType status, String message, UriInfo uriInfo) {
        String path = uriInfo == null ? null : uriInfo.getRequestUri().getPath();
        return new ErrorResponse(status.getStatusCode(), status.getReasonPhrase(), message, path, Instant.now());
    }
}
