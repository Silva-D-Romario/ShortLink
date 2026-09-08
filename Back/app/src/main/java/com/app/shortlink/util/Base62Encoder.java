package com.app.shortlink.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();

    public String encode(long value) {
        long remaining = value == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(value);
        if (remaining == 0) {
            return "0";
        }
        StringBuilder result = new StringBuilder();
        while (remaining > 0) {
            result.append(ALPHABET[(int) (remaining % 62)]);
            remaining /= 62;
        }
        return result.reverse().toString();
    }
}