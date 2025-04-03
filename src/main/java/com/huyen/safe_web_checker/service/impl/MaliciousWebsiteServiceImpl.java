package com.huyen.safe_web_checker.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.huyen.safe_web_checker.domain.MaliciousWebsite;
import com.huyen.safe_web_checker.repository.MaliciousWebsiteRepository;
import com.huyen.safe_web_checker.service.MaliciousWebsiteService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MaliciousWebsiteServiceImpl implements MaliciousWebsiteService {

    private final MaliciousWebsiteRepository maliciousWebsiteRepository;

    @Autowired
    public MaliciousWebsiteServiceImpl(MaliciousWebsiteRepository maliciousWebsiteRepository) {
        this.maliciousWebsiteRepository = maliciousWebsiteRepository;
    }

    @Override
    public Page<MaliciousWebsite> findAllWithFiltersPaged(String status, String impersonatedOrg, Pageable pageable) {
        if (status != null && impersonatedOrg != null) {
            return maliciousWebsiteRepository.findByStatusAndImpersonatedOrgContainingIgnoreCase(
                    status, impersonatedOrg, pageable);
        } else if (status != null) {
            return maliciousWebsiteRepository.findByStatus(status, pageable);
        } else if (impersonatedOrg != null) {
            return maliciousWebsiteRepository.findByImpersonatedOrgContainingIgnoreCase(impersonatedOrg, pageable);
        }
        return maliciousWebsiteRepository.findAll(pageable);
    }

    @Override
    public List<MaliciousWebsite> findAll() {
        return maliciousWebsiteRepository.findAll();
    }

    @Override
    public List<MaliciousWebsite> findAllWithFilters(String status, String impersonatedOrg) {
        if (status != null && impersonatedOrg != null) {
            return maliciousWebsiteRepository.findByStatusAndImpersonatedOrgContainingIgnoreCase(status,
                    impersonatedOrg);
        } else if (status != null) {
            return maliciousWebsiteRepository.findByStatus(status);
        } else if (impersonatedOrg != null) {
            return maliciousWebsiteRepository.findByImpersonatedOrgContainingIgnoreCase(impersonatedOrg);
        }
        return findAll();
    }

    @Override
    public Optional<MaliciousWebsite> findById(Integer id) {
        return maliciousWebsiteRepository.findById(id);
    }

    @Override
    public MaliciousWebsite save(MaliciousWebsite website) {
        if (website.getDetectionDate() == null) {
            website.setDetectionDate(new Date());
        }
        if (website.getStatus() == null) {
            website.setStatus("Đang xử lý");
        }
        return maliciousWebsiteRepository.save(website);
    }

    @Override
    public void deleteById(Integer id) {
        maliciousWebsiteRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return maliciousWebsiteRepository.existsById(id);
    }

    @Override
    public MaliciousWebsite updateStatus(Integer id, String newStatus) {
        MaliciousWebsite website = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Malicious website not found with id: " + id));

        website.setStatus(newStatus);
        return save(website);
    }
}