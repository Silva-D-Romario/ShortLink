package com.app.shortlink.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.shortlink.auth.dto.AuthRequest;
import com.app.shortlink.auth.dto.AuthResponse;
import com.app.shortlink.auth.dto.ProfileRequest;
import com.app.shortlink.auth.model.User;
import com.app.shortlink.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        return userRepository.findByAuthToken(token).map(User::getId);
    }

    public AuthResponse getProfile(Long userId) {
        return profileResponse(findUser(userId));
    }

    @Transactional
    public AuthResponse updateProfile(Long userId, ProfileRequest request) {
        User user = findUser(userId);
        String email = normalizeEmail(request.getEmail());
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> { throw new IllegalArgumentException("Email already registered"); });
        user.setEmail(email);
        user.setFullName(request.getFullName().trim());
        userRepository.save(user);
        return response(user);
    }

    private AuthResponse response(User user) {
        String token = UUID.randomUUID().toString();
        user.setAuthToken(token);
        userRepository.save(user);
        return profileResponse(user).toBuilder()
                .token(token)
                .build();
    }

    private AuthResponse profileResponse(User user) {
        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
