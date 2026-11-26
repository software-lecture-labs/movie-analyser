package com.movieanalyser.entity;

import jakarta.nosql.Column;
import jakarta.nosql.Convert;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Pattern;
import org.eclipse.jnosql.databases.mongodb.mapping.ObjectIdConverter;

/**
 * Kayıtlı kullanıcı; "users" koleksiyonunda saklanır.
 * Kullanıcı adı ve e-posta benzersizdir, bu unique index'lerle sağlanır. Düz parola
 * hiçbir zaman saklanmaz, yalnızca parola hash'leyicinin ürettiği hash saklanır.
 */
@Entity("users")
public class User {

    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 30;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    // Kodda String olarak görünür, MongoDB'de ObjectId olarak saklanır; böylece id ile arama eşleşir
    @Id
    @Convert(ObjectIdConverter.class)
    private String id;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String passwordHash;

    @Column
    @Convert(InstantDateConverter.class)
    private Instant createdAt;

    // Örtük public parametresiz constructor, eşleme katmanı entity'yi yeniden oluştururken kullanır.

    /**
     * Yeni bir kullanıcı oluşturur. Kullanıcı kaydedilene kadar id null kalır.
     * Kullanıcı adı ve e-posta, kontrol edilip saklanmadan önce baştaki/sondaki boşluklardan arındırılır.
     */
    public static User create(String username, String email, String passwordHash, Instant now) {
        String cleanUsername = requireNotBlank(username, "username");
        String cleanEmail = requireNotBlank(email, "email");
        requireNotBlank(passwordHash, "passwordHash");
        validateUsername(cleanUsername);
        validateEmail(cleanEmail);
        Objects.requireNonNull(now, "now");

        User user = new User();
        user.username = cleanUsername;
        user.email = cleanEmail;
        user.passwordHash = passwordHash;
        // MongoDB tarihleri milisaniye hassasiyetinde saklar; yuvarlamak bellekteki değeri
        // veritabanından okunan değerle aynı tutar.
        user.createdAt = now.truncatedTo(ChronoUnit.MILLIS);
        return user;
    }

    private static void validateUsername(String value) {
        if (value.length() < MIN_USERNAME_LENGTH || value.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException("username must be between " + MIN_USERNAME_LENGTH
                    + " and " + MAX_USERNAME_LENGTH + " characters");
        }
    }

    private static void validateEmail(String value) {
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("email is not a valid address");
        }
    }

    private static String requireNotBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
