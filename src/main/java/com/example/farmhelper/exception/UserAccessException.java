package com.example.farmhelper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserAccessException extends RuntimeException {

    public UserAccessException(String username) {
        super(String.format("You cannot remove a user with username = %s with ADMIN role", username));
    }
}
