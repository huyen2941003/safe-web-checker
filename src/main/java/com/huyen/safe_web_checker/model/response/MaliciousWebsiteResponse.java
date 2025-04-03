package com.huyen.safe_web_checker.model.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaliciousWebsiteResponse {
    private Integer id;
    private String websiteName;
    private Date detectionDate;
    private String impersonatedOrg;
    private String status;
    private Date createdAt;
}