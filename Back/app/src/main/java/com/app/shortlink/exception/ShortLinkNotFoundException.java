package com.app.shortlink.exception;

public class ShortLinkNotFoundException extends RuntimeException {
    public ShortLinkNotFoundException(String message) {
        super(message);
    }
}