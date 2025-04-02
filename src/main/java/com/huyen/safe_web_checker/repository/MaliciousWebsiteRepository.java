package com.huyen.safe_web_checker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.huyen.safe_web_checker.domain.MaliciousWebsite;

public interface MaliciousWebsiteRepository extends JpaRepository<MaliciousWebsite, Long> {
    @Query("SELECT m FROM MaliciousWebsite m WHERE m.websiteName LIKE %:url%")
    List<MaliciousWebsite> findSimilarWebsites(@Param("url") String url);
}
