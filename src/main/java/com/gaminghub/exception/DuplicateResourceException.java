package com.gaminghub.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends ApiException {
    public DuplicateResourceException(String resourceName, String value) {
        super(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", resourceName + " already exists: " + value);
    }
}
