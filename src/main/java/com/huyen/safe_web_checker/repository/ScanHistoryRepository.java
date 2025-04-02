package com.huyen.safe_web_checker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.huyen.safe_web_checker.domain.User;
import com.huyen.safe_web_checker.domain.UserScanHistory;

public interface ScanHistoryRepository extends JpaRepository<UserScanHistory, Long> {
    List<UserScanHistory> findByUserOrderByScanDateDesc(User user);

    long countByUserAndScanDateAfter(User user, LocalDateTime date);

    long countByUserIsNullAndScanDateAfter(LocalDateTime date);
}