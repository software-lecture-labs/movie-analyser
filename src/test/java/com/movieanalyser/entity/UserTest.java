package com.movieanalyser.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class UserTest {

    private static final String USERNAME = "seyma";
    private static final String EMAIL = "seyma@example.com";
    private static final String HASH = "pbkdf2_sha256$1$c2FsdA==$aGFzaA==";
    private static final Instant NOW = Instant.parse("2026-11-26T10:00:00Z");

    @Test
    void createSetsFieldsAndTimestamp() {
        User user = User.create(USERNAME, EMAIL, HASH, NOW);

        assertNull(user.getId());
        assertEquals(USERNAME, user.getUsername());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(HASH, user.getPasswordHash());
        assertEquals(NOW, user.getCreatedAt());
    }

    @Test
    void createTruncatesTimestampToMilliseconds() {
        Instant withNanos = Instant.parse("2026-11-26T10:00:00.123456789Z");

        User user = User.create(USERNAME, EMAIL, HASH, withNanos);

        assertEquals(Instant.parse("2026-11-26T10:00:00.123Z"), user.getCreatedAt());
    }

    @Test
    void createTrimsUsernameAndEmail() {
        User user = User.create("  seyma  ", "  seyma@example.com ", HASH, NOW);

        assertEquals(USERNAME, user.getUsername());
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    void createRejectsBlankUsername() {
        assertThrows(IllegalArgumentException.class, () -> User.create(null, EMAIL, HASH, NOW));
        assertThrows(IllegalArgumentException.class, () -> User.create("  ", EMAIL, HASH, NOW));
    }

    @Test
    void createRejectsUsernameOutsideAllowedLength() {
        assertThrows(IllegalArgumentException.class, () -> User.create("ab", EMAIL, HASH, NOW));
        assertThrows(IllegalArgumentException.class,
                () -> User.create("a".repeat(User.MAX_USERNAME_LENGTH + 1), EMAIL, HASH, NOW));
    }

    @Test
    void createAcceptsBoundaryUsernameLengths() {
        assertEquals(User.MIN_USERNAME_LENGTH,
                User.create("a".repeat(User.MIN_USERNAME_LENGTH), EMAIL, HASH, NOW)
                        .getUsername().length());
        assertEquals(User.MAX_USERNAME_LENGTH,
                User.create("a".repeat(User.MAX_USERNAME_LENGTH), EMAIL, HASH, NOW)
                        .getUsername().length());
    }

    @Test
    void createRejectsUsernameWithDisallowedCharacters() {
        // Kullanıcı adı URL'de ve ekranda kullanılacak: boşluk, @, emoji, / gibi karakterler olmaz
        String emoji = "seyma" + new String(Character.toChars(0x1F600));
        for (String bad : new String[] {"se yma", "seyma@x", emoji, "se/yma", "şeyma", "seyma!"}) {
            assertThrows(IllegalArgumentException.class, () -> User.create(bad, EMAIL, HASH, NOW), bad);
        }
    }

    @Test
    void createAcceptsLettersDigitsDotUnderscoreAndHyphen() {
        assertEquals("Seyma_2.x-y", User.create("Seyma_2.x-y", EMAIL, HASH, NOW).getUsername());
    }

    @Test
    void createStoresEmailInLowerCase() {
        // selim@Example.com ve Selim@example.com aynı adres sayılır, unique index ikisini de yakalar
        assertEquals("selim@example.com",
                User.create(USERNAME, "Selim@Example.COM", HASH, NOW).getEmail());
    }

    @Test
    void emailLowerCasingDoesNotDependOnServerLocale() {
        Locale original = Locale.getDefault();
        try {
            // Türkçe yerel ayarda "I".toLowerCase() noktasız ı verir; adres bozulmamalı
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("ilker@example.com",
                    User.create(USERNAME, "ILKER@EXAMPLE.COM", HASH, NOW).getEmail());
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void createRejectsBlankEmail() {
        assertThrows(IllegalArgumentException.class, () -> User.create(USERNAME, null, HASH, NOW));
        assertThrows(IllegalArgumentException.class, () -> User.create(USERNAME, " ", HASH, NOW));
    }

    @Test
    void createRejectsMalformedEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> User.create(USERNAME, "not-an-email", HASH, NOW));
        assertThrows(IllegalArgumentException.class,
                () -> User.create(USERNAME, "missing@domain", HASH, NOW));
        assertThrows(IllegalArgumentException.class,
                () -> User.create(USERNAME, "@example.com", HASH, NOW));
    }

    @Test
    void createRejectsBlankPasswordHash() {
        assertThrows(IllegalArgumentException.class,
                () -> User.create(USERNAME, EMAIL, null, NOW));
        assertThrows(IllegalArgumentException.class,
                () -> User.create(USERNAME, EMAIL, "", NOW));
    }

    @Test
    void createRejectsNullTimestamp() {
        assertThrows(NullPointerException.class, () -> User.create(USERNAME, EMAIL, HASH, null));
    }
}
