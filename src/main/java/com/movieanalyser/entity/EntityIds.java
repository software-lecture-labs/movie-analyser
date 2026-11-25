package com.movieanalyser.entity;

import org.bson.types.ObjectId;

/**
 * Checks the format of entity ids without exposing the MongoDB id type.
 *
 * <p>Ids are stored as MongoDB ObjectIds but handled as Strings everywhere else.
 * Services call this before a lookup, so that a malformed id from a request is
 * reported as "not found" instead of failing inside the mapping layer.</p>
 */
public final class EntityIds {

    private EntityIds() {
    }

    /**
     * Returns true if the value has the format of a stored id (24 hexadecimal characters).
     */
    public static boolean isValid(String id) {
        return id != null && ObjectId.isValid(id);
    }
}
