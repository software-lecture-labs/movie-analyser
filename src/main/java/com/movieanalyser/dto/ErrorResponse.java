package com.movieanalyser.dto;

import java.time.Instant;

/**
 * Bütün hata yanıtlarında kullanılan tek tip JSON gövdesi.
 *
 * @param status    HTTP durum kodu (örn. 404)
 * @param error     durum kodunun standart adı (örn. "Not Found")
 * @param message   istemciye gösterilecek açıklama; iç ayrıntı içermez
 * @param path      hatanın oluştuğu istek yolu, bilinmiyorsa null
 * @param timestamp hatanın oluştuğu an
 */
public record ErrorResponse(int status, String error, String message, String path, Instant timestamp) {
}
