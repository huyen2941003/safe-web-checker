package com.huyen.safe_web_checker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.huyen.safe_web_checker.domain.MaliciousWebsite;

public interface MaliciousWebsiteService {
    List<MaliciousWebsite> findAll();

    Page<MaliciousWebsite> findAllWithFiltersPaged(String status, String impersonatedOrg, Pageable pageable);

    List<MaliciousWebsite> findAllWithFilters(String status, String impersonatedOrg);

    Optional<MaliciousWebsite> findById(Integer id);

    MaliciousWebsite save(MaliciousWebsite website);

    void deleteById(Integer id);

    boolean existsById(Integer id);

    MaliciousWebsite updateStatus(Integer id, String newStatus);
}