package com.movieanalyser.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.constraints.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Afiş adresi sayfalarda {@code <img src>} olarak kullanılacağı için yalnızca http(s) adresleri
 * kabul edilir; {@code javascript:} veya {@code data:} gibi adresler 400 ile reddedilir.
 */
class CreateMovieRequestTest {

    private static Pattern posterUrlPattern() throws NoSuchFieldException {
        Pattern pattern = CreateMovieRequest.class.getDeclaredField("posterUrl").getAnnotation(Pattern.class);
        assertNotNull(pattern, "posterUrl için @Pattern tanımlı olmalı");
        return pattern;
    }

    private static boolean accepts(String url) throws NoSuchFieldException {
        // @Pattern değerin tamamının eşleşmesini ister, String.matches de öyle
        return url.matches(posterUrlPattern().regexp());
    }

    @Test
    void acceptsHttpAndHttpsAddresses() throws NoSuchFieldException {
        assertTrue(accepts("https://example.com/poster.jpg"));
        assertTrue(accepts("http://example.com/a/b.png?w=300"));
        assertTrue(accepts("HTTPS://EXAMPLE.COM/P.JPG"));
    }

    @Test
    void rejectsOtherSchemesAndMalformedAddresses() throws NoSuchFieldException {
        assertFalse(accepts("javascript:alert(1)"));
        assertFalse(accepts("data:image/png;base64,AAAA"));
        assertFalse(accepts("ftp://example.com/poster.jpg"));
        assertFalse(accepts("example.com/poster.jpg"));
        assertFalse(accepts("https://"));
        assertFalse(accepts("https://example.com/a b.jpg"));
    }

    @Test
    void messageIsTurkish() throws NoSuchFieldException {
        assertEquals("http veya https ile başlayan bir adres olmalı", posterUrlPattern().message());
    }
}
