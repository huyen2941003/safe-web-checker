package com.huyen.safe_web_checker.service;

import java.util.List;

import com.huyen.safe_web_checker.model.request.RoleRequest;
import com.huyen.safe_web_checker.model.response.RoleResponse;

public interface RoleService {
    RoleResponse createRole(RoleRequest roleRequest);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(long id);

    RoleResponse updateRole(long id, RoleRequest roleRequest);

    void deleteRole(long id);
}
