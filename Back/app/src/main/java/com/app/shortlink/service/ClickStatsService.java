package com.app.shortlink.service;


import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.app.shortlink.model.ShortLink;
import com.app.shortlink.repositoty.ClickStatsRepository;
import com.app.shortlink.repositoty.ShortLinkRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickStatsService {

    private final ClickStatsRepository clickStatsRepository;
    private final ShortLinkRepository shortLinkRepository;

    public Map<String, Object> getDetailedStats(String shortCode, String domain) {
        ShortLink shortLink = shortLinkRepository
            .findByShortCodeAndDomainAndIsActiveTrue(shortCode, domain)
            .orElseThrow(() -> new RuntimeException("Link not found"));

        Map<String, Object> stats = new HashMap<>();
        
        // Estatísticas básicas
        stats.put("totalClicks", shortLink.getClickCount());
        
        // Clicks por dia
        stats.put("clicksByDay", clickStatsRepository.findClicksGroupedByDay(shortLink.getId()));
        
        // Clicks por país
        stats.put("clicksByCountry", clickStatsRepository.findClicksGroupedByCountry(shortLink.getId()));
        
        // Clicks por dispositivo
        stats.put("clicksByDevice", clickStatsRepository.findClicksGroupedByDevice(shortLink.getId()));
        
        // Clicks por navegador
        stats.put("clicksByBrowser", clickStatsRepository.findClicksGroupedByBrowser(shortLink.getId()));
        
        // Últimos cliques
        stats.put("recentClicks", clickStatsRepository.findAllByShortLinkIdOrderByClickDateDesc(shortLink.getId()));
        
        return stats;
    }
}