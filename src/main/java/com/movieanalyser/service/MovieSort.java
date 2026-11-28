package com.movieanalyser.service;

import com.movieanalyser.exception.BusinessRuleException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Film listesinin sıralama seçenekleri; istekte {@code ?sort=} ile seçilir.
 */
public enum MovieSort {

    /** Başlığa göre A'dan Z'ye (varsayılan). */
    TITLE,

    /** En yüksek ortalama puan önce; eşitlikte daha çok yorum almış film önce. */
    RATING,

    /** En yeni yapım yılı önce; aynı yılda başlığa göre. */
    YEAR;

    /**
     * İstekteki değeri çözer; boşsa varsayılan, tanınmıyorsa geçerli seçenekleri
     * listeleyen {@link BusinessRuleException} (400).
     */
    public static MovieSort from(String value) {
        if (value == null || value.isBlank()) {
            return TITLE;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("sort şunlardan biri olmalı: " + options() + ", gelen: " + value, e);
        }
    }

    private static String options() {
        return Arrays.stream(values())
                .map(sort -> sort.name().toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(", "));
    }
}
