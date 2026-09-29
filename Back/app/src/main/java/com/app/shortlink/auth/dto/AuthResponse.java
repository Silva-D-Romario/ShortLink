package com.app.shortlink.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AuthResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String token;
}
