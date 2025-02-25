package org.ably.it_support.core.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEntityException extends BaseException {
    public DuplicateEntityException(String entityName, String field, Object value) {
        super(
            String.format("%s already exists with %s: %s", entityName, field, value),
            HttpStatus.CONFLICT,
            "DUPLICATE_ENTITY"
        );
    }
}

