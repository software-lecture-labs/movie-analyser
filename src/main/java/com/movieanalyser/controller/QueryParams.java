package com.movieanalyser.controller;

import com.movieanalyser.exception.BusinessRuleException;

/**
 * Sorgu parametrelerini sayıya çevirir.
 *
 * <p>JAX-RS, {@code int} olarak tanımlanmış bir parametreyi sayıya çeviremezse
 * (örneğin {@code ?page=abc}) isteği 404 ile reddeder; bu da yanıltıcıdır. Parametreler
 * metin olarak alınıp burada çevrilir ve hatalı değer anlaşılır bir 400 ile bildirilir.</p>
 */
final class QueryParams {

    static final int DEFAULT_PAGE = 1;
    static final int DEFAULT_SIZE = 20;

    private QueryParams() {
    }

    /** Değer yoksa varsayılanı döner; tam sayı değilse {@link BusinessRuleException} (400). */
    static int toInt(String value, String name, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new BusinessRuleException(name + " bir tam sayı olmalı, gelen: " + value, e);
        }
    }

    static int page(String value) {
        return toInt(value, "page", DEFAULT_PAGE);
    }

    static int size(String value) {
        return toInt(value, "size", DEFAULT_SIZE);
    }
}
