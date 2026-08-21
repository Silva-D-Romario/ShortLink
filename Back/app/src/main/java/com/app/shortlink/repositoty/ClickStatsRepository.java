package com.app.shortlink.repositoty;



import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.shortlink.model.ClickStats;

public interface ClickStatsRepository extends JpaRepository<ClickStats, Long> {

    List<ClickStats> findAllByShortLinkIdOrderByClickDateDesc(Long shortLinkId);

    @Query("SELECT DATE(c.clickDate) as date, COUNT(c) as count " +
           "FROM ClickStats c WHERE c.shortLinkId = :shortLinkId " +
           "GROUP BY DATE(c.clickDate) ORDER BY date DESC")
    List<Map<String, Object>> findClicksGroupedByDay(@Param("shortLinkId") Long shortLinkId);

    @Query("SELECT c.country, COUNT(c) as count FROM ClickStats c " +
           "WHERE c.shortLinkId = :shortLinkId GROUP BY c.country")
    List<Map<String, Object>> findClicksGroupedByCountry(@Param("shortLinkId") Long shortLinkId);

    @Query("SELECT c.deviceType, COUNT(c) as count FROM ClickStats c " +
           "WHERE c.shortLinkId = :shortLinkId GROUP BY c.deviceType")
    List<Map<String, Object>> findClicksGroupedByDevice(@Param("shortLinkId") Long shortLinkId);

    @Query("SELECT c.browser, COUNT(c) as count FROM ClickStats c " +
           "WHERE c.shortLinkId = :shortLinkId GROUP BY c.browser")
    List<Map<String, Object>> findClicksGroupedByBrowser(@Param("shortLinkId") Long shortLinkId);

    long countByShortLinkId(Long shortLinkId);
}