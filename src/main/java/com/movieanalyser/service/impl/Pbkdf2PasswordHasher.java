package com.movieanalyser.service.impl;

import com.movieanalyser.service.PasswordHasher;
import jakarta.enterprise.context.ApplicationScoped;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Parolaları PBKDF2 (HMAC-SHA256) ve her parola için rastgele bir salt ile hash'ler;
 * yalnızca JDK kullanır.
 *
 * <p>Saklama biçimi: {@code pbkdf2_sha256$<iterasyon>$<salt base64>$<hash base64>}.
 * İterasyon sayısı saklanan değerin içindedir; böylece sonradan artırılsa bile mevcut
 * hash'ler geçersiz olmaz.</p>
 */
@ApplicationScoped
public class Pbkdf2PasswordHasher implements PasswordHasher {

    // OWASP Password Storage Cheat Sheet'in PBKDF2-HMAC-SHA256 önerisi
    // (210.000 SHA512 için önerilen değerdir, burada SHA256 kullanılıyor)
    static final int DEFAULT_ITERATIONS = 600_000;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String PREFIX = "pbkdf2_sha256";
    private static final String SEPARATOR = "$";
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private int iterations;

    /** CDI ve uygulama tarafından kullanılır; iterasyon sayısı güncel önerilere uygundur. */
    public Pbkdf2PasswordHasher() {
        this(DEFAULT_ITERATIONS);
    }

    /** Testlerin hızlı kalması için küçük bir iterasyon sayısı kullanılmasını sağlar. */
    Pbkdf2PasswordHasher(int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("iterations must be positive");
        }
        this.iterations = iterations;
    }

    @Override
    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] key = derive(rawPassword, salt, iterations);
        Base64.Encoder encoder = Base64.getEncoder();
        return String.join(SEPARATOR, PREFIX, String.valueOf(iterations),
                encoder.encodeToString(salt), encoder.encodeToString(key));
    }

    @Override
    public boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || rawPassword.isEmpty() || storedHash == null) {
            return false;
        }
        String[] parts = storedHash.split("\\" + SEPARATOR, -1);
        if (parts.length != 4 || !PREFIX.equals(parts[0])) {
            return false;
        }
        try {
            int storedIterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            if (storedIterations <= 0 || expected.length == 0) {
                return false;
            }
            byte[] actual = derive(rawPassword, salt, storedIterations, expected.length * 8);
            // Sabit zamanlı karşılaştırma; süre farkından hash'in ne kadarının eşleştiği anlaşılmaz.
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            // Sayı veya Base64 çözümlenemedi: saklanan değer bizim ürettiğimiz bir hash değil.
            return false;
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterationCount) {
        return derive(password, salt, iterationCount, KEY_BITS);
    }

    private static byte[] derive(String password, byte[] salt, int iterationCount, int keyBits) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyBits);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing is not available", e);
        } finally {
            spec.clearPassword();
        }
    }
}
