package com.huyen.safe_web_checker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huyen.safe_web_checker.model.request.RoleRequest;
import com.huyen.safe_web_checker.model.response.RoleResponse;
import com.huyen.safe_web_checker.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleRequest roleRequest) {
        RoleResponse response = roleService.createRole(roleRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        List<RoleResponse> response = roleService.getAllRoles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable long id) {
        RoleResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable long id, @RequestBody RoleRequest roleRequest) {
        RoleResponse response = roleService.updateRole(id, roleRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok("Role deleted successfully");
    }
}