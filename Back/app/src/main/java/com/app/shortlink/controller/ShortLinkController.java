package com.app.shortlink.controller;

import com.app.shortlink.dto.ShortLinkRequest;
import com.app.shortlink.dto.ShortLinkResponse;
import com.app.shortlink.dto.ClickStatsResponse;
import com.app.shortlink.service.ShortLinkService;
import com.app.shortlink.service.ClickStatsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/shortlinks")
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;
    private final ClickStatsService clickStatsService;

    // 1. Criar link encurtado
    @PostMapping("/shorten")
    public ResponseEntity<ShortLinkResponse> createShortLink(
            @Valid @RequestBody ShortLinkRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        ShortLinkResponse response = shortLinkService.createShortLink(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Redirecionamento (endpoint público)
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            @RequestParam(defaultValue = "sl") String domain,
            HttpServletRequest request) {

        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        String originalUrl = shortLinkService.getOriginalUrl(shortCode, domain, clientIp, userAgent);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    // 3. Listar links do usuário
    @GetMapping("/my-links")
    public ResponseEntity<Page<ShortLinkResponse>> getUserLinks(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        Long userId = getUserId(authentication);
        return ResponseEntity.ok(shortLinkService.getUserLinks(userId, pageable));
    }

    // 4. Atualizar link
    @PutMapping("/{linkId}")
    public ResponseEntity<ShortLinkResponse> updateLink(
            @PathVariable Long linkId,
            @Valid @RequestBody ShortLinkRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        return ResponseEntity.ok(shortLinkService.updateShortLink(linkId, request, userId));
    }

    // 5. Deletar link
    @DeleteMapping("/{linkId}")
    public ResponseEntity<Void> deleteLink(
            @PathVariable Long linkId,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        shortLinkService.deleteShortLink(linkId, userId);
        return ResponseEntity.noContent().build();
    }

    // 6. Estatísticas básicas
    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<ClickStatsResponse> getLinkStats(
            @PathVariable String shortCode,
            @RequestParam(defaultValue = "sl") String domain) {

        return ResponseEntity.ok(shortLinkService.getLinkStats(shortCode, domain));
    }

    // 7. Estatísticas detalhadas
    @GetMapping("/stats/{shortCode}/detailed")
    public ResponseEntity<Map<String, Object>> getDetailedStats(
            @PathVariable String shortCode,
            @RequestParam(defaultValue = "sl") String domain) {

        return ResponseEntity.ok(clickStatsService.getDetailedStats(shortCode, domain));
    }

    // Métodos auxiliares
    private Long getUserId(Authentication authentication) {
        return authentication == null ? 1L : Long.parseLong(authentication.getName());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }
}