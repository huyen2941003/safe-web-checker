package com.huyen.safe_web_checker.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
    @NotBlank(message = "Role name không được để trống")
    private String roleName;
}