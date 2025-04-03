package com.huyen.safe_web_checker.model.request;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class MaliciousWebsiteRequest {
    @NotBlank(message = "Website name is required")
    private String websiteName;

    private Date detectionDate;

    @Size(max = 255, message = "Impersonated organization must be less than 255 characters")
    private String impersonatedOrg;

    @Size(max = 50, message = "Status must be less than 50 characters")
    private String status;
}