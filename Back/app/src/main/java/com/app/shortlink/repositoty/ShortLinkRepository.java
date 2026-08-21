package com.app.shortlink.repositoty;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.app.shortlink.model.ShortLink;

public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {

    // Busca principal - usada no redirecionamento
    Optional<ShortLink> findByShortCodeAndDomainAndIsActiveTrue(String shortCode, String domain);

    // Verifica se já existe um link ativo para essa URL e usuário
    Optional<ShortLink> findByUrlHashAndUserIdAndIsActiveTrue(String urlHash, Long userId);

    // Lista todos os links de um usuário com paginação
    Page<ShortLink> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Busca por código (sem considerar domínio)
    Optional<ShortLink> findByShortCode(String shortCode);

    // Links expirados para limpeza automática
    List<ShortLink> findAllByExpiresAtBeforeAndIsActiveTrue(LocalDateTime now);

    // Atualização do contador de cliques (evita race condition)
    @Modifying
    @Transactional
    @Query("UPDATE ShortLink s SET s.clickCount = s.clickCount + 1 " +
           "WHERE s.shortCode = :shortCode AND s.domain = :domain AND s.isActive = true")
    int incrementClickCount(@Param("shortCode") String shortCode, 
                           @Param("domain") String domain);

    // Desativa links expirados
    @Modifying
    @Transactional
    @Query("UPDATE ShortLink s SET s.isActive = false " +
           "WHERE s.expiresAt IS NOT NULL AND s.expiresAt < :now")
    int deactivateExpiredLinks(@Param("now") LocalDateTime now);

    // Estatísticas do usuário
    @Query("SELECT COUNT(s) FROM ShortLink s WHERE s.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(s.clickCount) FROM ShortLink s WHERE s.userId = :userId")
    Long sumClickCountByUserId(@Param("userId") Long userId);

    // Top links mais clicados de um usuário
    @Query("SELECT s FROM ShortLink s WHERE s.userId = :userId AND s.isActive = true " +
           "ORDER BY s.clickCount DESC")
    List<ShortLink> findTopLinksByUserId(@Param("userId") Long userId, Pageable pageable);
}