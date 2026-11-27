package com.movieanalyser.entity;

import jakarta.nosql.AttributeConverter;
import java.time.Instant;
import java.util.Date;

/**
 * {@link Instant} değerlerini metin yerine BSON tarihi olarak saklar; böylece
 * MongoDB bunları zamana göre sıralayabilir ve filtreleyebilir.
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
