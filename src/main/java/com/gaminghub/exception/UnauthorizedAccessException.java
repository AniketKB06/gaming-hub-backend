package com.gaminghub.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends ApiException {
    public UnauthorizedAccessException(String message) {
        super(HttpStatus.FORBIDDEN, "UNAUTHORIZED_ACCESS", message);
    }
}
