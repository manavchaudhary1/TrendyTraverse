package com.manav.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class KeycloakConnectionException extends RuntimeException {
    public KeycloakConnectionException(String message) {
        super(message);
    }

    public KeycloakConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
