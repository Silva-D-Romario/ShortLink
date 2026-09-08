package com.app.shortlink.exception;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(String message) {
        super(message);
    }
}