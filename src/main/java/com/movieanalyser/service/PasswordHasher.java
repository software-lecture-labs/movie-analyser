package com.movieanalyser.service;

/**
 * Düz parolaları saklanması güvenli hash'lere çevirir ve bir düz parolayı saklanan hash'e
 * karşı doğrular. Çağıranlar hash'lemeden sonra düz parolayı görmez ve saklamaz.
 */
public interface PasswordHasher {

    /**
     * Parolayı her seferinde yeni rastgele bir salt ile hash'ler.
     *
     * @throws IllegalArgumentException parola null veya boşsa
     */
    String hash(String rawPassword);

    /**
     * Parolayı {@link #hash(String)} ile üretilmiş bir hash'e karşı doğrular.
     * Parola boşsa veya saklanan hash bozuksa hata fırlatmaz, false döner.
     */
    boolean matches(String rawPassword, String storedHash);
}
