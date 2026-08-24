package com.app.shortlink.dto;



import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortLinkResponse {
    private Long id;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private Integer clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private String notes;
}