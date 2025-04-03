package com.huyen.safe_web_checker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.huyen.safe_web_checker.domain.MaliciousWebsite;

public interface MaliciousWebsiteRepository extends JpaRepository<MaliciousWebsite, Long> {
    @Query("SELECT m FROM MaliciousWebsite m WHERE m.websiteName LIKE %:url%")
    List<MaliciousWebsite> findSimilarWebsites(@Param("url") String url);

    List<MaliciousWebsite> findByStatus(String status);

    List<MaliciousWebsite> findByImpersonatedOrgContainingIgnoreCase(String impersonatedOrg);

    List<MaliciousWebsite> findByStatusAndImpersonatedOrgContainingIgnoreCase(String status, String impersonatedOrg);

    Optional<MaliciousWebsite> deleteById(Integer id);

    boolean existsById(Integer id);

    Optional<MaliciousWebsite> findById(Integer id);

    Page<MaliciousWebsite> findByStatus(String status, Pageable pageable);

    Page<MaliciousWebsite> findByImpersonatedOrgContainingIgnoreCase(String impersonatedOrg, Pageable pageable);

    Page<MaliciousWebsite> findByStatusAndImpersonatedOrgContainingIgnoreCase(String status, String impersonatedOrg,
            Pageable pageable);
}
