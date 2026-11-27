package com.movieanalyser.exception.mapper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Daha özel bir mapper'ı olmayan bütün hataları yakalar.
 * JAX-RS'in kendi hataları (bilinmeyen yol 404, bozuk istek 400, desteklenmeyen metot 405)
 * durum kodlarını korur; diğer her şey 500 olur. İç ayrıntı istemciye gönderilmez, yalnızca log'lanır.
 */
@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

    static final String INTERNAL_ERROR_MESSAGE = "Beklenmeyen bir hata oluştu";

    private static final Logger LOGGER = Logger.getLogger(UncaughtExceptionMapper.class.getName());

    private final UriInfo uriInfo;

    // toResponse yalnızca exception'ı aldığı için UriInfo constructor'da @Context ile alınır.
    public UncaughtExceptionMapper(@Context UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException webException) {
            return fromWebApplicationException(webException);
        }
        LOGGER.log(Level.SEVERE, "Beklenmeyen hata", exception);
        return ErrorResponses.build(Response.Status.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MESSAGE, uriInfo);
    }

    // Orijinal yanıtın durum kodu ve başlıkları (örn. 405'teki Allow) korunur, yalnızca gövde değişir.
    private Response fromWebApplicationException(WebApplicationException exception) {
        Response original = exception.getResponse();
        Response.StatusType status = original.getStatusInfo();
        if (status.getFamily() == Response.Status.Family.SERVER_ERROR) {
            LOGGER.log(Level.SEVERE, "Sunucu hatası", exception);
        }
        return Response.fromResponse(original)
                .entity(ErrorResponses.body(status, genericMessage(status.getStatusCode()), uriInfo))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    // Exception'ın kendi mesajı iç ayrıntı içerebileceği için durum koduna göre genel bir mesaj seçilir.
    private static String genericMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "İstek geçersiz; gövdeyi ve parametreleri kontrol edin";
            case 401 -> "Kimlik doğrulama gerekli";
            case 403 -> "Bu işlem için yetkiniz yok";
            case 404 -> "İstenen kaynak bulunamadı";
            case 405 -> "Bu HTTP metodu bu adreste desteklenmiyor";
            case 406 -> "İstenen yanıt biçimi desteklenmiyor";
            case 415 -> "Gönderilen içerik türü desteklenmiyor";
            default -> statusCode >= 500 ? INTERNAL_ERROR_MESSAGE : "İstek işlenemedi";
        };
    }
}
