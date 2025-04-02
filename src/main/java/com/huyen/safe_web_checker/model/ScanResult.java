package com.huyen.safe_web_checker.model;

import com.huyen.safe_web_checker.domain.MaliciousWebsite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScanResult {
    private boolean malicious;
    private int riskScore;
    private String details;
    private MaliciousWebsite matchedWebsite;
}