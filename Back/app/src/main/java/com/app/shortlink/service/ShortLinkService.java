package com.app.shortlink.service;


import com.app.shortlink.dto.ShortLinkRequest;
import com.app.shortlink.dto.ShortLinkResponse;
import com.app.shortlink.dto.ClickStatsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShortLinkService {
    
    ShortLinkResponse createShortLink(ShortLinkRequest request, Long userId);
    
    String getOriginalUrl(String shortCode, String domain, String clientIp, String userAgent);
    
    Page<ShortLinkResponse> getUserLinks(Long userId, Pageable pageable);
    
    ShortLinkResponse updateShortLink(Long linkId, ShortLinkRequest request, Long userId);
    
    void deleteShortLink(Long linkId, Long userId);
    
    ClickStatsResponse getLinkStats(String shortCode, String domain);
    
    void deactivateExpiredLinks();
}