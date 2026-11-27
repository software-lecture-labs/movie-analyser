package com.movieanalyser.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Pbkdf2PasswordHasherTest {

    private static final String PASSWORD = "S3cret-pass";

    // Düşük iterasyon sayısı testleri hızlı tutar; biçim üretimdekiyle aynıdır.
    private final Pbkdf2PasswordHasher hasher = new Pbkdf2PasswordHasher(1_000);

    @Test
    void defaultIterationsFollowOwaspRecommendationForSha256() {
        // OWASP Password Storage Cheat Sheet: PBKDF2-HMAC-SHA256 için 600.000
        // (210.000, PBKDF2-HMAC-SHA512 için önerilen değer)
        assertEquals(600_000, Pbkdf2PasswordHasher.DEFAULT_ITERATIONS);
    }

    @Test
    void hashDoesNotContainThePlainPassword() {
        String hash = hasher.hash(PASSWORD);

        assertFalse(hash.contains(PASSWORD));
    }

    @Test
    void hashUsesTheDocumentedFormat() {
        String[] parts = hasher.hash(PASSWORD).split("\\$");

        assertEquals(4, parts.length);
        assertEquals("pbkdf2_sha256", parts[0]);
        assertEquals("1000", parts[1]);
    }

    @Test
    void samePasswordProducesDifferentHashesBecauseOfSalt() {
        assertNotEquals(hasher.hash(PASSWORD), hasher.hash(PASSWORD));
    }

    @Test
    void matchesAcceptsTheCorrectPassword() {
        String hash = hasher.hash(PASSWORD);

        assertTrue(hasher.matches(PASSWORD, hash));
    }

    @Test
    void matchesRejectsAWrongPassword() {
        String hash = hasher.hash(PASSWORD);

        assertFalse(hasher.matches("wrong-pass", hash));
        assertFalse(hasher.matches(PASSWORD + " ", hash));
    }

    @Test
    void matchesUsesTheIterationCountStoredInTheHash() {
        String hash = new Pbkdf2PasswordHasher(2_000).hash(PASSWORD);

        assertTrue(hasher.matches(PASSWORD, hash));
    }

    @Test
    void matchesReturnsFalseForMalformedHashes() {
        assertFalse(hasher.matches(PASSWORD, null));
        assertFalse(hasher.matches(PASSWORD, ""));
        assertFalse(hasher.matches(PASSWORD, "plain-text"));
        assertFalse(hasher.matches(PASSWORD, "pbkdf2_sha256$abc$c2FsdA==$aGFzaA=="));
        assertFalse(hasher.matches(PASSWORD, "pbkdf2_sha256$1000$not base64$aGFzaA=="));
        assertFalse(hasher.matches(PASSWORD, "md5$1000$c2FsdA==$aGFzaA=="));
    }

    @Test
    void hashRejectsBlankPassword() {
        assertThrows(IllegalArgumentException.class, () -> hasher.hash(null));
        assertThrows(IllegalArgumentException.class, () -> hasher.hash(""));
        assertThrows(IllegalArgumentException.class, () -> hasher.hash("   "));
    }

    @Test
    void matchesReturnsFalseForBlankPassword() {
        String hash = hasher.hash(PASSWORD);

        assertFalse(hasher.matches(null, hash));
        assertFalse(hasher.matches("", hash));
    }

    @Test
    void constructorRejectsNonPositiveIterations() {
        assertThrows(IllegalArgumentException.class, () -> new Pbkdf2PasswordHasher(0));
    }
}
