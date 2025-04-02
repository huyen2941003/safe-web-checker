package com.huyen.safe_web_checker.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.huyen.safe_web_checker.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
}