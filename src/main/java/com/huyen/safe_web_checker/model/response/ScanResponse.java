package com.huyen.safe_web_checker.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanResponse {
    private String url;
    private boolean isMalicious;
    private String detectedAs;
    private LocalDateTime scanTime;
    private Integer remainingScans;
    private String riskLevel;
}