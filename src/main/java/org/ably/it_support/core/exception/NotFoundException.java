package org.ably.it_support.core.exception;

import org.springframework.http.HttpStatus;


public class NotFoundException extends BaseException {
    public NotFoundException(String entityName, Object entityId) {
        super(
            String.format("%s not found with id: %s", entityName, entityId),
            HttpStatus.NOT_FOUND,
            "NOT_FOUND"
        );
    }
}