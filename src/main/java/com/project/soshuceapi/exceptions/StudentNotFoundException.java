package com.project.soshuceapi.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class StudentNotFoundException extends UsernameNotFoundException {
    public StudentNotFoundException(String msg) {
        super(msg);
    }

    public StudentNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
