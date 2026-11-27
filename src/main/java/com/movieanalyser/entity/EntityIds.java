package com.movieanalyser.entity;

import org.bson.types.ObjectId;

/**
 * Entity id'lerinin biçimini, MongoDB id tipini dışarı açmadan kontrol eder.
 *
 * <p>Id'ler MongoDB ObjectId olarak saklanır, ancak başka her yerde String olarak işlenir.
 * Servisler arama yapmadan önce bunu çağırır; böylece istekten gelen hatalı biçimli bir id,
 * eşleme katmanında hata vermek yerine "bulunamadı" olarak bildirilir.</p>
 */
public final class EntityIds {

    private EntityIds() {
    }

    /**
     * Değer saklanan bir id biçimindeyse (24 onaltılık karakter) true döndürür.
     */
    public static boolean isValid(String id) {
        return id != null && ObjectId.isValid(id);
    }
}
