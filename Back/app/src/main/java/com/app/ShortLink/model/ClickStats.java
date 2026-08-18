package com.app.ShortLink.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "click_stats", 
       indexes = {
           @Index(name = "idx_short_link_id", columnList = "shortLinkId"),
           @Index(name = "idx_click_date", columnList = "clickDate")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long shortLinkId;

    @Column(nullable = false)
    private String shortCode;

    @Column(nullable = false)
    private String domain;

    @Column
    private String ipAddress;

    @Column
    private String userAgent;

    @Column
    private String referer;

    @Column
    private String country;

    @Column
    private String city;

    @Column
    private String deviceType; // mobile, desktop, tablet

    @Column
    private String browser;

    @Column
    private String os;

    @CreationTimestamp
    private LocalDateTime clickDate;
}