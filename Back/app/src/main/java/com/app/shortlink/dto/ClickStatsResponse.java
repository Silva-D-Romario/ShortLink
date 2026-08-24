package com.app.shortlink.dto;



import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClickStatsResponse {
    private String shortCode;
    private String originalUrl;
    private Integer totalClicks;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean isActive;
}