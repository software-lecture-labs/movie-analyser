package com.movieanalyser.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EntityIdsTest {

    @Test
    void acceptsObjectIdHex() {
        assertTrue(EntityIds.isValid("6b060c259c09bf675bb8c53e"));
    }

    @Test
    void rejectsNull() {
        assertFalse(EntityIds.isValid(null));
    }

    @Test
    void rejectsBlank() {
        assertFalse(EntityIds.isValid(" "));
    }

    @Test
    void rejectsShortValue() {
        assertFalse(EntityIds.isValid("abc"));
    }

    @Test
    void rejectsNonHex() {
        assertFalse(EntityIds.isValid("zzzzzzzzzzzzzzzzzzzzzzzz"));
    }
}
