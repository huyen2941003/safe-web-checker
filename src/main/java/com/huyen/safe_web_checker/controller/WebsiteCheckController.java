package com.huyen.safe_web_checker.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huyen.safe_web_checker.domain.User;
import com.huyen.safe_web_checker.domain.UserScanHistory;
import com.huyen.safe_web_checker.model.ScanResult;
import com.huyen.safe_web_checker.repository.ScanHistoryRepository;
import com.huyen.safe_web_checker.repository.UserRepository;
import com.huyen.safe_web_checker.security.JwtTokenProvider;
import com.huyen.safe_web_checker.service.WebsiteCheckService;
import com.huyen.safe_web_checker.utils.exception.ScanLimitExceededException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/v1/scan")
public class WebsiteCheckController {
    private final WebsiteCheckService websiteCheckService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ScanHistoryRepository scanHistoryRepository;

    @Autowired
    public WebsiteCheckController(WebsiteCheckService websiteCheckService, JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository, ScanHistoryRepository scanHistoryRepository) {
        this.websiteCheckService = websiteCheckService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.scanHistoryRepository = scanHistoryRepository;
    }

    @PostMapping
    public ResponseEntity<?> scanWebsite(@RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        String url = request.get("url");
        if (url == null || url.isEmpty()) {
            return ResponseEntity.badRequest().body("URL không được để trống");
        }

        try {
            ScanResult result = websiteCheckService.checkWebsite(url, httpRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("url", url);
            response.put("isMalicious", result.isMalicious());
            response.put("riskScore", result.getRiskScore());
            response.put("details", result.getDetails());

            if (result.getMatchedWebsite() != null) {
                Map<String, Object> matchedSite = new HashMap<>();
                matchedSite.put("id", result.getMatchedWebsite().getId());
                matchedSite.put("websiteName", result.getMatchedWebsite().getWebsiteName());
                matchedSite.put("impersonatedOrg", result.getMatchedWebsite().getImpersonatedOrg());
                matchedSite.put("detectionDate", result.getMatchedWebsite().getDetectionDate());
                response.put("matchedWebsite", matchedSite);
            }

            return ResponseEntity.ok(response);
        } catch (ScanLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi quét website");
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getScanHistory(HttpServletRequest httpRequest) {
        String token = getJwtFromRequest(httpRequest);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Vui lòng đăng nhập để xem lịch sử");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<UserScanHistory> history = scanHistoryRepository.findByUserOrderByScanDateDesc(user);

        List<Map<String, Object>> response = history.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> convertToResponse(UserScanHistory history) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", history.getId());
        item.put("websiteUrl", history.getWebsiteUrl());
        item.put("scanDate", history.getScanDate());
        item.put("isMalicious", history.isMalicious());
        item.put("riskScore", history.getRiskScore());

        if (history.getMatchedWebsite() != null) {
            Map<String, Object> matchedSite = new HashMap<>();
            matchedSite.put("id", history.getMatchedWebsite().getId());
            matchedSite.put("websiteName", history.getMatchedWebsite().getWebsiteName());
            item.put("matchedWebsite", matchedSite);
        }

        return item;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}