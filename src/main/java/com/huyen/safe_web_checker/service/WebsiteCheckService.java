package com.huyen.safe_web_checker.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.huyen.safe_web_checker.domain.MaliciousWebsite;
import com.huyen.safe_web_checker.domain.User;
import com.huyen.safe_web_checker.domain.UserScanHistory;
import com.huyen.safe_web_checker.model.ScanResult;
import com.huyen.safe_web_checker.repository.MaliciousWebsiteRepository;
import com.huyen.safe_web_checker.repository.UserRepository;
import com.huyen.safe_web_checker.repository.ScanHistoryRepository;
import com.huyen.safe_web_checker.security.JwtTokenProvider;
import com.huyen.safe_web_checker.utils.exception.ScanLimitExceededException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class WebsiteCheckService {
    private final ScanHistoryRepository scanHistoryRepository;
    private final MaliciousWebsiteRepository maliciousWebsiteRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${max.checks.per.day:3}")
    private int maxChecksPerDay;

    @Autowired
    public WebsiteCheckService(ScanHistoryRepository scanHistoryRepository,
            MaliciousWebsiteRepository maliciousWebsiteRepository,
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider) {
        this.scanHistoryRepository = scanHistoryRepository;
        this.maliciousWebsiteRepository = maliciousWebsiteRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ScanResult checkWebsite(String url, HttpServletRequest request) {
        String token = getJwtFromRequest(request);
        User user = null;

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        checkScanLimit(user, request);
        ScanResult result = performWebsiteCheck(url);
        saveScanHistory(url, result, user);

        if (user != null) {
            updateUserScanInfo(user);
        }

        return result;
    }

    private ScanResult performWebsiteCheck(String url) {
        List<MaliciousWebsite> matchedWebsites = maliciousWebsiteRepository.findSimilarWebsites(url);

        if (!matchedWebsites.isEmpty()) {
            return new ScanResult(true, 100,
                    "Website matches known malicious sites in our database",
                    matchedWebsites.get(0));
        }

        boolean isMalicious = isUrlMalicious(url);
        int riskScore = calculateRiskScore(url);
        String details = generateScanDetails(url, isMalicious, riskScore);

        return new ScanResult(isMalicious, riskScore, details, null);
    }

    private void checkScanLimit(User user, HttpServletRequest request) {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);

        if (user == null) {
            long scansInLast24Hours = scanHistoryRepository.countByUserIsNullAndScanDateAfter(last24Hours);

            if (scansInLast24Hours >= maxChecksPerDay) {
                throw new ScanLimitExceededException(
                        "Bạn đã vượt quá giới hạn " + maxChecksPerDay
                                + " lần kiểm tra mỗi ngày. Vui lòng đăng nhập để tiếp tục.");
            }
        } else {

        }
    }

    private void saveScanHistory(String url, ScanResult result, User user) {
        UserScanHistory history = new UserScanHistory();
        history.setWebsiteUrl(url);
        history.setMalicious(result.isMalicious());
        history.setRiskScore(result.getRiskScore());
        history.setScanDetails(result.getDetails());
        history.setScanDate(Date.valueOf(LocalDate.now()));
        history.setMatchedWebsite(result.getMatchedWebsite());
        history.setUser(user);

        scanHistoryRepository.save(history);
    }

    private void updateUserScanInfo(User user) {
        user.setScanCount(user.getScanCount() + 1);
        user.setLastScanDate(Date.valueOf(LocalDate.now()));
        userRepository.save(user);
    }

    // Các phương thức hỗ trợ
    private boolean isUrlMalicious(String url) {
        return url.contains("phishing") || url.contains("scam");
    }

    private int calculateRiskScore(String url) {
        if (url.contains("phishing"))
            return 100;
        if (url.contains("scam"))
            return 90;
        if (url.contains("suspicious"))
            return 70;
        return 10;
    }

    private String generateScanDetails(String url, boolean isMalicious, int riskScore) {
        return String.format("Scan result for %s: %s (Risk score: %d)",
                url, isMalicious ? "MALICIOUS" : "SAFE", riskScore);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}