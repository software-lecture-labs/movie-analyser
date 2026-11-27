package com.movieanalyser.service.impl;

import com.movieanalyser.exception.BusinessRuleException;
import jakarta.data.page.PageRequest;

/**
 * Dışarıdan gelen sayfa bilgisini doğrulayıp {@link PageRequest}'e çevirir.
 * Sayfa numarası 1'den başlar; çok büyük sayfalar tek istekte belleğe alınmasın
 * diye sayfa boyutunun bir üst sınırı vardır.
 */
public final class Paging {

    public static final int MAX_SIZE = 100;

    /**
     * Atlanacak kayıt sayısı veritabanı sürücüsünde int'e çevrildiği için çok büyük sayfa
     * numaraları taşmaya (500) yol açar; ayrıca o kadar derin sayfalar gerçekçi değildir.
     */
    public static final int MAX_PAGE = 10_000;

    private Paging() {
    }

    /** Geçersiz değerlerde {@link BusinessRuleException} (400) fırlatır. */
    public static PageRequest of(int page, int size) {
        if (page < 1 || page > MAX_PAGE) {
            throw new BusinessRuleException("page 1 ile " + MAX_PAGE + " arasında olmalı, gelen: " + page);
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new BusinessRuleException("size 1 ile " + MAX_SIZE + " arasında olmalı, gelen: " + size);
        }
        return PageRequest.ofPage(page).size(size);
    }
}
