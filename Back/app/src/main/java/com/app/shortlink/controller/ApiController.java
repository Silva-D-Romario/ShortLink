package com.app.shortlink.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @GetMapping({ "/", "/health" })
    public Map<String, String> health() {
        return Map.of(
                "application", "shortlink",
                "status", "UP",
                "message", "ShortLink API is running");
    }
}
