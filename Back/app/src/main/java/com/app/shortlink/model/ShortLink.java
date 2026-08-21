package com.app.shortlink.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "short_links", 
       indexes = {
           @Index(name = "idx_short_code_domain", columnList = "shortCode, domain"),
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_url_hash", columnList = "urlHash")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 20)
    private String shortCode;

    @Column(nullable = false, length = 50)
    private String domain; // "sl", "go", "link" etc

    @Column(nullable = false)
    private String urlHash; // MD5 da URL original para busca rápida

    @Column(nullable = false)
    private Long userId; // Referência ao usuário que criou

    @Column(nullable = false)
    private Integer clickCount = 0;

    @Column
    private LocalDateTime expiresAt; // null = nunca expira

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String notes; // Anotações pessoais

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Método para incrementar cliques
    public void incrementClickCount() {
        this.clickCount++;
    }
}