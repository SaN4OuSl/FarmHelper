package com.example.farmhelper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserAccessException extends RuntimeException {

    public UserAccessException(String username) {
        super(String.format("Your user with username %s doesn't have enough rights ", username));
    }
}
