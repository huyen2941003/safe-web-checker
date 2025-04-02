package com.huyen.safe_web_checker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huyen.safe_web_checker.domain.Role;
import com.huyen.safe_web_checker.model.request.RoleRequest;
import com.huyen.safe_web_checker.model.response.RoleResponse;
import com.huyen.safe_web_checker.repository.RoleRepository;
import com.huyen.safe_web_checker.service.RoleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {
        if (roleRepository.existsByRoleName(roleRequest.getRoleName())) {
            throw new RuntimeException("Role name đã tồn tại!");
        }
        Role role = new Role();
        role.setRoleName(roleRequest.getRoleName());
        role = roleRepository.save(role);
        return new RoleResponse(role);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRoleById(long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role có id: " + id));
        return new RoleResponse(role);
    }

    @Override
    public RoleResponse updateRole(long id, RoleRequest roleRequest) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role có id: " + id));

        role.setRoleName(roleRequest.getRoleName());

        role = roleRepository.save(role);
        return new RoleResponse(role);
    }

    @Override
    public void deleteRole(long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role có id: " + id));

        roleRepository.delete(role);
    }
}