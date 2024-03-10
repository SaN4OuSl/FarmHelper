package com.example.farmhelper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImpossibleAmount extends RuntimeException {
    public ImpossibleAmount() {
        super("There is no such amount of this harvest in stock");
    }
}
