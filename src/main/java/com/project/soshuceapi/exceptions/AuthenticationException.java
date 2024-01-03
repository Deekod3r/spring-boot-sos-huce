package com.project.soshuceapi.exceptions;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException {

    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthenticationException(String msg) {
        super(msg);
    }
}
