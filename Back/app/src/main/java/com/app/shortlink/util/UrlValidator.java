package com.app.shortlink.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Component;

@Component
public class UrlValidator {

    public boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(normalize(value));
            return ("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null;
        } catch (URISyntaxException exception) {
            return false;
        }
    }

    public String normalize(String value) {
        String trimmed = value.trim();
        return trimmed.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")
                ? trimmed
                : "https://" + trimmed;
    }
}