package com.example.farmhelper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExist extends RuntimeException {

    public ResourceAlreadyExist() {
        super("Resource already exist");
    }

    public ResourceAlreadyExist(String typeOfResource) {
        super(String.format("%s already exist", typeOfResource));
    }

    public ResourceAlreadyExist(String typeOfResource, String resource) {
        super(String.format("%s for this %s already exists", typeOfResource, resource));
    }

    public ResourceAlreadyExist(String typeOfResource, String fieldOfResource, String resource) {
        super(String.format("%s with %s: %s already exist", typeOfResource, fieldOfResource,
            resource));
    }
}