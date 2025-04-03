package com.huyen.safe_web_checker.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "malicious_websites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaliciousWebsite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "website_name", nullable = false, columnDefinition = "TEXT")
    private String websiteName;

    @Column(name = "detection_date")
    @Temporal(TemporalType.DATE)
    private Date detectionDate;

    @Column(name = "impersonated_org", length = 255)
    private String impersonatedOrg;

    @Column(name = "status", length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "matchedWebsite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserScanHistory> scanHistories;

    // Các phương thức tiện ích
    public boolean isActive() {
        return "Đang xử lý".equals(this.status);
    }

    public boolean isResolved() {
        return "Đã xử lý".equals(this.status);
    }

    public static class MaliciousWebsiteBuilder {
        private String status = "Đang xử lý";
        private Date detectionDate = new Date();
    }
}