package com.app.shortlink.auth.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.shortlink.auth.dto.AuthRequest;
import com.app.shortlink.auth.dto.AuthResponse;
import com.app.shortlink.auth.model.User;
import com.app.shortlink.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    @Transactional
    public AuthResponse register(AuthRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .isActive(true)
                .build();
        return response(userRepository.save(user));
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.getEmail())).orElse(null);
        if (user == null || !user.getIsActive() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return response(user);
    }

    public Optional<Long> resolveUserId(String token) {
        return Optional.ofNullable(sessions.get(token));
    }

    private AuthResponse response(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user.getId());
        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .token(token)
                .build();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
