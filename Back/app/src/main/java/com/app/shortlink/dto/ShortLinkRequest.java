package com.app.shortlink.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class ShortLinkRequest {
    
    @NotBlank(message = "URL is required")
    @Pattern(
        regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$",
        message = "Invalid URL format"
    )
    private String originalUrl;
    
    @Pattern(regexp = "^[a-z0-9]{2,10}$", message = "Domain must be alphanumeric, 2-10 chars")
    private String domain = "sl";
    
    @Max(value = 365, message = "Expiration cannot exceed 365 days")
    private Integer expiresInDays;
    
    private String notes;
}