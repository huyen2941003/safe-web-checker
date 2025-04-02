package com.huyen.safe_web_checker.model.response;

import com.huyen.safe_web_checker.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    private long id;
    private String role;

    public RoleResponse(Role role) {
        this.id = role.getId();
        this.role = role.getRoleName();
    }
}