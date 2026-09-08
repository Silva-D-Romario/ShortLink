package com.app.shortlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class ShortLinkRequest {

    @NotBlank(message = "URL is required")
    private String originalUrl;

    private String domain = "sl";

    @Max(value = 365, message = "Expiration cannot exceed 365 days")
    private Integer expiresInDays;

    private String notes;
}