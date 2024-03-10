package com.example.farmhelper.controller;

import com.example.farmhelper.config.UserPrincipal;
import com.example.farmhelper.util.SecurityContextUtils;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
@AllArgsConstructor
public class SecurityController {


    @GetMapping("/principal")
    public UserPrincipal getPrincipal() {
        return SecurityContextUtils.getPrincipal();
    }
}
