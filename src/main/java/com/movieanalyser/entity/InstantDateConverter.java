package com.movieanalyser.entity;

import jakarta.nosql.AttributeConverter;
import java.time.Instant;
import java.util.Date;

/**
 * Stores {@link Instant} values as BSON dates instead of strings, so that
 * MongoDB can sort and filter them by time.
 */
public class InstantDateConverter implements AttributeConverter<Instant, Date> {

    @Override
    public Date convertToDatabaseColumn(Instant attribute) {
        return attribute == null ? null : Date.from(attribute);
    }

    @Override
    public Instant convertToEntityAttribute(Date dbData) {
        return dbData == null ? null : dbData.toInstant();
    }
}
