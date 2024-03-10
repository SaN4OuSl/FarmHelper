package com.example.farmhelper.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@AllArgsConstructor
public class HealthController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String getStatus() {
        return "Healthy!!!";
    }
}
