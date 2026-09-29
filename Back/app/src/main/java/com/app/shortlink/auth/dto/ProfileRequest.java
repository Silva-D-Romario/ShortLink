package com.app.shortlink.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String fullName;
}
