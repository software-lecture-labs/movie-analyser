package com.movieanalyser.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

class InstantDateConverterTest {

    private final InstantDateConverter converter = new InstantDateConverter();

    @Test
    void convertsInstantToDateWithSameMoment() {
        Instant instant = Instant.parse("2026-11-25T10:15:30.123Z");

        assertEquals(Date.from(instant), converter.convertToDatabaseColumn(instant));
    }

    @Test
    void convertsDateBackToInstant() {
        Instant instant = Instant.parse("2026-11-25T10:15:30.123Z");

        assertEquals(instant, converter.convertToEntityAttribute(Date.from(instant)));
    }

    @Test
    void keepsNullAsNullInBothDirections() {
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
    }
}
