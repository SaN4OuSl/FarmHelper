package com.example.farmhelper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvoiceIsAlreadyCompleted extends RuntimeException {
    public InvoiceIsAlreadyCompleted() {
        super("This invoice has already been completed");
    }

    public InvoiceIsAlreadyCompleted(Long id) {
        super(String.format("Invoice with id = %s has already been completed", id));
    }
}
