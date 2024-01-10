package com.project.soshuceapi.exceptions;

public class UserExistedException extends RuntimeException {

        public UserExistedException(String message) {
            super(message);
        }

}
