package com.huyen.safe_web_checker.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "user_scan_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "website_url", nullable = false, columnDefinition = "TEXT")
    private String websiteUrl;

    @Column(name = "scan_date", nullable = false, updatable = false)
    @CreationTimestamp
    private Date scanDate;

    @Column(name = "is_malicious", nullable = false)
    private boolean isMalicious;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_website_id", nullable = true)
    private MaliciousWebsite matchedWebsite;

    // Có thể thêm các trường bổ sung nếu cần
    @Column(name = "risk_score", nullable = true)
    private Integer riskScore; // Điểm rủi ro từ 0-100

    @Column(name = "scan_details", columnDefinition = "TEXT", nullable = true)
    private String scanDetails; // Chi tiết kết quả quét dạng JSON
}