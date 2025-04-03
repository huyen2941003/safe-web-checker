package com.huyen.safe_web_checker.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huyen.safe_web_checker.domain.MaliciousWebsite;
import com.huyen.safe_web_checker.model.request.MaliciousWebsiteRequest;
import com.huyen.safe_web_checker.model.response.MaliciousWebsiteResponse;
import com.huyen.safe_web_checker.model.response.PageResponse;
import com.huyen.safe_web_checker.service.MaliciousWebsiteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/malicious-websites")
public class MaliciousWebsiteController {

    private final MaliciousWebsiteService maliciousWebsiteService;

    @Autowired
    public MaliciousWebsiteController(MaliciousWebsiteService maliciousWebsiteService) {
        this.maliciousWebsiteService = maliciousWebsiteService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<MaliciousWebsiteResponse>> getAllWebsites(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String impersonatedOrg,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<MaliciousWebsite> websitePage = maliciousWebsiteService.findAllWithFiltersPaged(
                status, impersonatedOrg, pageable);

        return ResponseEntity.ok(buildPageResponse(websitePage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaliciousWebsiteResponse> getWebsiteById(@PathVariable Integer id) {
        MaliciousWebsite website = maliciousWebsiteService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Malicious website not found with id: " + id));

        return ResponseEntity.ok(convertToResponse(website));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaliciousWebsiteResponse> createWebsite(
            @Valid @RequestBody MaliciousWebsiteRequest request) {

        MaliciousWebsite website = convertToEntity(request);
        MaliciousWebsite savedWebsite = maliciousWebsiteService.save(website);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToResponse(savedWebsite));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaliciousWebsiteResponse> updateWebsite(
            @PathVariable Integer id,
            @Valid @RequestBody MaliciousWebsiteRequest request) {

        if (!maliciousWebsiteService.existsById(id)) {
            throw new ResourceNotFoundException("Malicious website not found with id: " + id);
        }

        MaliciousWebsite website = convertToEntity(request);
        website.setId(id);
        MaliciousWebsite updatedWebsite = maliciousWebsiteService.save(website);

        return ResponseEntity.ok(convertToResponse(updatedWebsite));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWebsite(@PathVariable Integer id) {
        if (!maliciousWebsiteService.existsById(id)) {
            throw new ResourceNotFoundException("Malicious website not found with id: " + id);
        }

        maliciousWebsiteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaliciousWebsiteResponse> updateStatus(
            @PathVariable Integer id,
            @RequestParam String newStatus) {

        MaliciousWebsite updatedWebsite = maliciousWebsiteService.updateStatus(id, newStatus);
        return ResponseEntity.ok(convertToResponse(updatedWebsite));
    }

    // Helper methods
    private MaliciousWebsiteResponse convertToResponse(MaliciousWebsite website) {
        return MaliciousWebsiteResponse.builder()
                .id(website.getId())
                .websiteName(website.getWebsiteName())
                .detectionDate(website.getDetectionDate())
                .impersonatedOrg(website.getImpersonatedOrg())
                .status(website.getStatus())
                .createdAt(website.getCreatedAt())
                .build();
    }

    private MaliciousWebsite convertToEntity(MaliciousWebsiteRequest request) {
        return MaliciousWebsite.builder()
                .websiteName(request.getWebsiteName())
                .detectionDate(request.getDetectionDate())
                .impersonatedOrg(request.getImpersonatedOrg())
                .status(request.getStatus())
                .build();
    }

    private Sort parseSort(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort != null) {
            for (String sortParam : sort) {
                String[] _sort = sortParam.split(",");
                if (_sort.length >= 2) {
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            }
        }

        // Mặc định sắp xếp theo ID giảm dần nếu không có sort
        if (orders.isEmpty()) {
            orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        }

        return Sort.by(orders);
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }

    private PageResponse<MaliciousWebsiteResponse> buildPageResponse(Page<MaliciousWebsite> page) {
        return PageResponse.<MaliciousWebsiteResponse>builder()
                .content(page.getContent().stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}