package com.app.shortlink.auth.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.app.shortlink.auth.dto.AuthRequest;
import com.app.shortlink.auth.dto.AuthResponse;

@Service
public class AuthService {

    private final AtomicLong nextUserId = new AtomicLong(1);
    private final Map<String, RegisteredUser> users = new ConcurrentHashMap<>();

    public AuthResponse register(AuthRequest request) {
        long userId = nextUserId.getAndIncrement();
        users.putIfAbsent(request.getEmail(), new RegisteredUser(userId, request.getPassword()));
        return response(request.getEmail(), users.get(request.getEmail()).userId());
    }

    public AuthResponse login(AuthRequest request) {
        RegisteredUser user = users.get(request.getEmail());
        if (user == null || !user.password().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return response(request.getEmail(), user.userId());
    }

    private AuthResponse response(String email, long userId) {
        return AuthResponse.builder()
            .userId(userId)
            .email(email)
            .token("dev-user-" + userId)
            .build();
    }

    private record RegisteredUser(long userId, String password) {
    }
}