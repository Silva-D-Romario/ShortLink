package com.app.shortlink.service;



import com.app.shortlink.dto.ShortLinkRequest;
import com.app.shortlink.dto.ShortLinkResponse;
import com.app.shortlink.dto.ClickStatsResponse;
import com.app.shortlink.exception.ShortLinkNotFoundException;
import com.app.shortlink.exception.InvalidUrlException;
import com.app.shortlink.model.ShortLink;
import com.app.shortlink.model.ClickStats;
import com.app.shortlink.repository.ShortLinkRepository;
import com.app.shortlink.repository.ClickStatsRepository;
import com.app.shortlink.util.Base62Encoder;
import com.app.shortlink.util.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    private final ShortLinkRepository shortLinkRepository;
    private final ClickStatsRepository clickStatsRepository;
    private final Base62Encoder base62Encoder;
    private final UrlValidator urlValidator;

    @Override
    @Transactional
    public ShortLinkResponse createShortLink(ShortLinkRequest request, Long userId) {
        // 1. Valida a URL
        if (!urlValidator.isValid(request.getOriginalUrl())) {
            throw new InvalidUrlException("Invalid URL format: " + request.getOriginalUrl());
        }

        String normalizedUrl = urlValidator.normalize(request.getOriginalUrl());
        String urlHash = generateHash(normalizedUrl);
        String domain = request.getDomain() != null ? request.getDomain() : "sl";

        // 2. Verifica se já existe link ativo para essa URL (opcional)
        // Se quiser permitir duplicatas, comente esta parte
        return shortLinkRepository.findByUrlHashAndUserIdAndIsActiveTrue(urlHash, userId)
            .map(existingLink -> {
                log.info("Link already exists for user {}: {}", userId, existingLink.getShortCode());
                return mapToResponse(existingLink);
            })
            .orElseGet(() -> {
                // 3. Gera novo link
                String shortCode = generateUniqueShortCode();
                
                ShortLink newLink = ShortLink.builder()
                    .originalUrl(normalizedUrl)
                    .shortCode(shortCode)
                    .domain(domain)
                    .urlHash(urlHash)
                    .userId(userId)
                    .expiresAt(request.getExpiresInDays() != null ? 
                               LocalDateTime.now().plusDays(request.getExpiresInDays()) : null)
                    .isActive(true)
                    .clickCount(0)
                    .notes(request.getNotes())
                    .build();

                ShortLink saved = shortLinkRepository.save(newLink);
                log.info("Short link created: {} -> {} for user {}", shortCode, normalizedUrl, userId);
                
                return mapToResponse(saved);
            });
    }

    @Override
    @Cacheable(value = "shortLinkCache", key = "#domain + ':' + #shortCode")
    public String getOriginalUrl(String shortCode, String domain, String clientIp, String userAgent) {
        // 1. Busca o link ativo
        ShortLink shortLink = shortLinkRepository
            .findByShortCodeAndDomainAndIsActiveTrue(shortCode, domain)
            .orElseThrow(() -> new ShortLinkNotFoundException("Link not found or expired"));

        // 2. Verifica expiração
        if (shortLink.getExpiresAt() != null && shortLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            shortLink.setIsActive(false);
            shortLinkRepository.save(shortLink);
            throw new ShortLinkNotFoundException("Link has expired");
        }

        // 3. Incrementa contador de cliques (síncrono ou async)
        shortLinkRepository.incrementClickCount(shortCode, domain);
        shortLink.incrementClickCount();

        // 4. Salva estatísticas de clique (assíncrono)
        saveClickStatsAsync(shortLink.getId(), shortCode, domain, clientIp, userAgent);

        return shortLink.getOriginalUrl();
    }

    @Async
    public void saveClickStatsAsync(Long shortLinkId, String shortCode, String domain, 
                                   String ipAddress, String userAgent) {
        try {
            ClickStats stats = ClickStats.builder()
                .shortLinkId(shortLinkId)
                .shortCode(shortCode)
                .domain(domain)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .clickDate(LocalDateTime.now())
                .build();
            
            // Opcional: enriquecer com geolocalização via API externa
            // enrichWithGeoLocation(stats, ipAddress);
            
            clickStatsRepository.save(stats);
        } catch (Exception e) {
            log.error("Error saving click stats: {}", e.getMessage());
        }
    }

    @Override
    public Page<ShortLinkResponse> getUserLinks(Long userId, Pageable pageable) {
        return shortLinkRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "shortLinkCache", key = "#domain + ':' + #shortCode")
    public ShortLinkResponse updateShortLink(Long linkId, ShortLinkRequest request, Long userId) {
        ShortLink shortLink = shortLinkRepository.findById(linkId)
            .orElseThrow(() -> new ShortLinkNotFoundException("Link not found"));

        if (!shortLink.getUserId().equals(userId)) {
            throw new SecurityException("You don't have permission to update this link");
        }

        if (request.getOriginalUrl() != null && !request.getOriginalUrl().isEmpty()) {
            if (!urlValidator.isValid(request.getOriginalUrl())) {
                throw new InvalidUrlException("Invalid URL format");
            }
            shortLink.setOriginalUrl(urlValidator.normalize(request.getOriginalUrl()));
            shortLink.setUrlHash(generateHash(shortLink.getOriginalUrl()));
        }

        if (request.getExpiresInDays() != null) {
            shortLink.setExpiresAt(LocalDateTime.now().plusDays(request.getExpiresInDays()));
        }

        if (request.getNotes() != null) {
            shortLink.setNotes(request.getNotes());
        }

        ShortLink updated = shortLinkRepository.save(shortLink);
        log.info("Short link updated: {}", updated.getShortCode());
        
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "shortLinkCache", key = "#domain + ':' + #shortCode")
    public void deleteShortLink(Long linkId, Long userId) {
        ShortLink shortLink = shortLinkRepository.findById(linkId)
            .orElseThrow(() -> new ShortLinkNotFoundException("Link not found"));

        if (!shortLink.getUserId().equals(userId)) {
            throw new SecurityException("You don't have permission to delete this link");
        }

        // Soft delete
        shortLink.setIsActive(false);
        shortLinkRepository.save(shortLink);
        log.info("Short link deleted: {}", shortLink.getShortCode());
    }

    @Override
    public ClickStatsResponse getLinkStats(String shortCode, String domain) {
        ShortLink shortLink = shortLinkRepository
            .findByShortCodeAndDomainAndIsActiveTrue(shortCode, domain)
            .orElseThrow(() -> new ShortLinkNotFoundException("Link not found"));

        return ClickStatsResponse.builder()
            .shortCode(shortLink.getShortCode())
            .originalUrl(shortLink.getOriginalUrl())
            .totalClicks(shortLink.getClickCount())
            .createdAt(shortLink.getCreatedAt())
            .expiresAt(shortLink.getExpiresAt())
            .isActive(shortLink.getIsActive())
            .build();
    }

    @Override
    @Transactional
    public void deactivateExpiredLinks() {
        int count = shortLinkRepository.deactivateExpiredLinks(LocalDateTime.now());
        if (count > 0) {
            log.info("Deactivated {} expired links", count);
        }
    }

    // Métodos privados auxiliares
    private String generateUniqueShortCode() {
        // Tenta gerar um código único (com tentativas)
        int attempts = 0;
        while (attempts < 5) {
            // Usa Base62 com UUID para garantir aleatoriedade
            String code = base62Encoder.encode(UUID.randomUUID().getMostSignificantBits());
            // Garante tamanho mínimo de 6 caracteres
            while (code.length() < 6) {
                code = "0" + code;
            }
            
            // Verifica se já existe
            if (shortLinkRepository.findByShortCode(code).isEmpty()) {
                return code;
            }
            attempts++;
        }
        throw new RuntimeException("Failed to generate unique short code");
    }

    private String generateHash(String url) {
        return org.springframework.util.DigestUtils.md5DigestAsHex(url.getBytes());
    }

    private ShortLinkResponse mapToResponse(ShortLink link) {
        String shortUrl = String.format("http://%s.seudominio.com/%s", 
            link.getDomain(), 
            link.getShortCode()
        );
        
        return ShortLinkResponse.builder()
            .id(link.getId())
            .shortCode(link.getShortCode())
            .shortUrl(shortUrl)
            .originalUrl(link.getOriginalUrl())
            .clickCount(link.getClickCount())
            .createdAt(link.getCreatedAt())
            .expiresAt(link.getExpiresAt())
            .isActive(link.getIsActive())
            .notes(link.getNotes())
            .build();
    }
}